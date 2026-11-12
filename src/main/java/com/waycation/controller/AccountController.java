package com.waycation.controller;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller handling portal sign-in, self-registration, and account settings views.
 */
    // Spring MVC Web Controller handling incoming HTTP requests and returning views
@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Route HTTP GET request for tourist sign-in view
    @GetMapping("/tourist/sign-in")
    public String touristSignInPage(Model model) {
        model.addAttribute("portalRole", Role.TOURIST.name());
        return "auth/sign-in";
    }

    @GetMapping("/owner/sign-in")
    public String ownerSignInPage(Model model) {
        model.addAttribute("portalRole", Role.HOTEL_OWNER.name());
        return "auth/sign-in";
    }

    @GetMapping("/admin/sign-in")
    public String adminSignInPage(Model model) {
        model.addAttribute("portalRole", Role.ADMIN.name());
        return "auth/sign-in";
    }

    // Route HTTP POST request for authenticating user credentials
    @PostMapping("/auth/sign-in")
    public String authenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "portalRole", defaultValue = "TOURIST") String portalRole,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Role expectedRole;
        try {
            expectedRole = Role.valueOf(portalRole.toUpperCase());
        } catch (IllegalArgumentException ex) {
            expectedRole = Role.TOURIST;
        }

        try {
            Profile profile = accountService.authenticateUser(email, password, expectedRole);
            // Save authenticated user UID into HTTP session for access control
            session.setAttribute("uid", profile.getFirebaseUid());
            // Save user role into HTTP session for authorization checks
            session.setAttribute("role", profile.getRole().name());
            session.setAttribute("displayName", profile.getDisplayName());

            // Redirect user to their role-specific dashboard upon successful sign-in
            return switch (profile.getRole()) {
                case HOTEL_OWNER -> "redirect:/owner/dashboard";
                case ADMIN, SUPER_ADMIN -> "redirect:/admin/dashboard";
                default -> "redirect:/account";
            };
        } catch (Exception ex) {
            // Flash message: survives redirect (Post/Redirect/Get pattern) to show alert banner
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return switch (expectedRole) {
                case HOTEL_OWNER -> "redirect:/owner/sign-in";
                case ADMIN, SUPER_ADMIN -> "redirect:/admin/sign-in";
                default -> "redirect:/tourist/sign-in";
            };
        }
    }

    @GetMapping("/sign-up")
    public String signUpPage(@RequestParam(value = "role", defaultValue = "TOURIST") String role, Model model) {
        model.addAttribute("selectedRole", role.toUpperCase());
        return "auth/sign-up";
    }

    @PostMapping("/auth/sign-up")
    public String register(
            @RequestParam("displayName") String displayName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "role", defaultValue = "TOURIST") String roleStr,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            Profile profile = accountService.registerUserAccount(displayName, email, password, role);
            // Save authenticated user UID into HTTP session for access control
            session.setAttribute("uid", profile.getFirebaseUid());
            // Save user role into HTTP session for authorization checks
            session.setAttribute("role", profile.getRole().name());
            session.setAttribute("displayName", profile.getDisplayName());
            redirectAttributes.addFlashAttribute("successMessage", "Welcome to waYcation, " + profile.getDisplayName() + "!");
            return role == Role.HOTEL_OWNER ? "redirect:/owner/dashboard" : "redirect:/account";
        } catch (Exception ex) {
            // Flash message: survives redirect (Post/Redirect/Get pattern) to show alert banner
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/sign-up?role=" + roleStr;
        }
    }

    @GetMapping("/account")
    public String accountPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null || uid.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in to access your account settings.");
            return "redirect:/tourist/sign-in";
        }
        try {
            Profile profile = accountService.getProfile(uid);
            model.addAttribute("profile", profile);
            return "auth/account";
        } catch (Exception ex) {
            // Invalidate and terminate current HTTP session on user sign-out
        session.invalidate();
            return "redirect:/tourist/sign-in";
        }
    }

    @PostMapping("/account/update")
    public String updateAccount(
            @RequestParam("displayName") String displayName,
            @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null || uid.isBlank()) {
            return "redirect:/tourist/sign-in";
        }
        try {
            Profile updated = accountService.updateProfile(uid, displayName, avatarUrl);
            session.setAttribute("displayName", updated.getDisplayName());
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception ex) {
            // Flash message: survives redirect (Post/Redirect/Get pattern) to show alert banner
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/account/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        String uid = (String) session.getAttribute("uid");
        if (uid != null && !uid.isBlank()) {
            accountService.requestAccountDeletion(uid);
            // Invalidate and terminate current HTTP session on user sign-out
        session.invalidate();
        }
        redirectAttributes.addFlashAttribute("successMessage", "Your account has been anonymized and signed out.");
        return "redirect:/tourist/sign-in";
    }
}








