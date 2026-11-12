package com.waycation.controller;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.service.AccountService;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for the Administrative User & Role Governance Console (/admin/users).
 * Strictly restricted to users with ADMIN or SUPER_ADMIN privileges.
 */
@Controller
public class AdminUserController {

    private final AccountService accountService;

    public AdminUserController(AccountService accountService) {
        this.accountService = accountService;
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        // Verify admin role authorization from session before loading dashboard
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role) || "SUPER_ADMIN".equals(role);
    }

    @GetMapping("/admin/users")
    public String showUserGovernance(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roleFilter", required = false, defaultValue = "ALL") String roleFilter,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Administrator role required to access User Governance.");
            return "redirect:/admin/sign-in?redirect=/admin/users";
        }

        String callerUid = (String) session.getAttribute("uid");
        String callerRole = (String) session.getAttribute("role");

        try {
            List<Profile> allUsers = accountService.getAllUsersForAdmin(callerUid);

            long totalUsers = allUsers.size();
            long touristCount = allUsers.stream().filter(u -> u.getRole() == Role.TOURIST).count();
            long hotelierCount = allUsers.stream().filter(u -> u.getRole() == Role.HOTEL_OWNER).count();
            long staffCount = allUsers.stream().filter(u -> u.getRole() == Role.STAFF).count();
            long adminCount = allUsers.stream().filter(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.SUPER_ADMIN).count();

            List<Profile> filteredUsers = allUsers.stream()
                    .filter(u -> {
                        if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equalsIgnoreCase(roleFilter)) {
                            return u.getRole().name().equalsIgnoreCase(roleFilter);
                        }
                        return true;
                    })
                    .filter(u -> {
                        if (search != null && !search.isBlank()) {
                            String term = search.trim().toLowerCase();
                            String name = u.getDisplayName() != null ? u.getDisplayName().toLowerCase() : "";
                            String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                            return name.contains(term) || email.contains(term);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            model.addAttribute("users", filteredUsers);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("touristCount", touristCount);
            model.addAttribute("hotelierCount", hotelierCount);
            model.addAttribute("staffCount", staffCount);
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("search", search != null ? search : "");
            model.addAttribute("roleFilter", roleFilter != null ? roleFilter.toUpperCase() : "ALL");
            model.addAttribute("allRoles", Role.values());
            model.addAttribute("callerRole", callerRole);
            model.addAttribute("callerUid", callerUid);

            return "admin/users";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/packages";
        }
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateUserRole(
            @PathVariable("id") String targetUserId,
            @RequestParam("role") String newRoleStr,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roleFilter", required = false) String roleFilter,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Administrator role required.");
            return "redirect:/admin/sign-in?redirect=/admin/users";
        }

        String callerUid = (String) session.getAttribute("uid");

        try {
            Role targetRole = Role.valueOf(newRoleStr.toUpperCase());
            Profile updated = accountService.updateUserRoleByAdmin(callerUid, targetUserId, targetRole);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Role successfully updated to " + updated.getRole().name() + " for " + updated.getDisplayName() + " (" + updated.getEmail() + ").");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user role: " + ex.getMessage());
        }

        StringBuilder redirectUrl = new StringBuilder("/admin/users");
        boolean hasParam = false;
        if (search != null && !search.isBlank()) {
            redirectUrl.append("?search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
            hasParam = true;
        }
        if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equalsIgnoreCase(roleFilter)) {
            redirectUrl.append(hasParam ? "&" : "?").append("roleFilter=").append(URLEncoder.encode(roleFilter, StandardCharsets.UTF_8));
        }

        return "redirect:" + redirectUrl.toString();
    }
}

