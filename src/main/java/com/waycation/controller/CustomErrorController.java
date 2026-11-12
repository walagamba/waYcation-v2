package com.waycation.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global Error Controller and Friendly Route Alias Handler.
 * Replaces Spring Boot default Whitelabel Error Page with a high-fidelity
 * Nothing OS & Apple minimalist error page, and routes common URLs seamlessly.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = 500;
        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        String uri = requestUri != null ? requestUri.toString() : "";

        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception instanceof Throwable t) {
            org.slf4j.LoggerFactory.getLogger(CustomErrorController.class).error(
                    "Error occurred while processing request: " + uri + " (HTTP " + statusCode + ")", t);
        }

        // Friendly redirect shortcuts if typed directly
        if ("/sign-in".equalsIgnoreCase(uri) || "/login".equalsIgnoreCase(uri)) {
            return "redirect:/tourist/sign-in";
        }
        if ("/sign-out".equalsIgnoreCase(uri) || "/logout".equalsIgnoreCase(uri)) {
            return "redirect:/sign-out";
        }
        if ("/register".equalsIgnoreCase(uri)) {
            return "redirect:/sign-up";
        }
        if ("/bookings".equalsIgnoreCase(uri) || "/my-bookings".equalsIgnoreCase(uri)) {
            return "redirect:/tourist/bookings";
        }
        if ("/reviews".equalsIgnoreCase(uri) || "/my-reviews".equalsIgnoreCase(uri)) {
            return "redirect:/tourist/reviews";
        }
        if ("/admin".equalsIgnoreCase(uri) || "/admin/dashboard".equalsIgnoreCase(uri)) {
            return "redirect:/admin/users";
        }
        if ("/owner".equalsIgnoreCase(uri) || "/owner/dashboard".equalsIgnoreCase(uri)) {
            return "redirect:/owner/hotels";
        }
        if ("/coupons".equalsIgnoreCase(uri)) {
            return "redirect:/admin/coupons";
        }
        if ("/users".equalsIgnoreCase(uri)) {
            return "redirect:/admin/users";
        }
        if ("/home".equalsIgnoreCase(uri) || "/index".equalsIgnoreCase(uri)) {
            return "redirect:/";
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("statusTitle", statusCode == 404 ? "Signal Lost · 404" : "System Error · " + statusCode);
        model.addAttribute("statusMessage", statusCode == 404
                ? "The requested destination or coordinate could not be located in our verified registry."
                : (message != null && !message.toString().isBlank() ? message.toString() : "An unexpected server condition occurred."));
        model.addAttribute("requestedUri", uri);

        return "error";
    }

    // Direct alias route mappings so they never hit /error in the first place
    @GetMapping({"/sign-in", "/login"})
    public String aliasSignIn() {
        return "redirect:/tourist/sign-in";
    }

    @GetMapping("/register")
    public String aliasRegister() {
        return "redirect:/sign-up";
    }

    @GetMapping({"/sign-out", "/logout"})
    public String aliasSignOut(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out.");
        return "redirect:/";
    }

    @GetMapping({"/bookings", "/my-bookings"})
    public String aliasBookings() {
        return "redirect:/tourist/bookings";
    }

    @GetMapping({"/reviews", "/my-reviews"})
    public String aliasReviews() {
        return "redirect:/tourist/reviews";
    }

    @GetMapping({"/admin", "/admin/dashboard"})
    public String aliasAdmin() {
        return "redirect:/admin/users";
    }

    @GetMapping({"/owner", "/owner/dashboard"})
    public String aliasOwner() {
        return "redirect:/owner/hotels";
    }

    @GetMapping("/coupons")
    public String aliasCoupons() {
        return "redirect:/admin/coupons";
    }

    @GetMapping("/users")
    public String aliasUsers() {
        return "redirect:/admin/users";
    }

    @GetMapping({"/home", "/index"})
    public String aliasHome() {
        return "redirect:/";
    }
}
