package com.waycation.controller;

import com.waycation.model.Hotel;
import com.waycation.model.PackageHotel;
import com.waycation.model.PackagePurchase;
import com.waycation.model.PackageStatus;
import com.waycation.model.TravelPackage;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.PackagePurchaseRepository;
import com.waycation.service.PackageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PackageController {

    private final PackageService packageService;
    private final HotelRepository hotelRepository;
    private final PackagePurchaseRepository packagePurchaseRepository;

    public PackageController(
            PackageService packageService,
            HotelRepository hotelRepository,
            PackagePurchaseRepository packagePurchaseRepository) {
        this.packageService = packageService;
        this.hotelRepository = hotelRepository;
        this.packagePurchaseRepository = packagePurchaseRepository;
    }

    @GetMapping("/packages")
    public String viewCatalog(Model model) {
        List<TravelPackage> packages = packageService.getPackages(true);
        model.addAttribute("packages", packages);
        return "packages/catalog";
    }

    @GetMapping("/packages/{id}")
    public String viewPackageDetail(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean purchased,
            HttpSession session,
            Model model) {
        TravelPackage travelPackage = packageService.getPackageById(id);
        long purchaseCount = packagePurchaseRepository.countByPackageId(id);

        String currentUid = resolveSessionUid(session, "tourist-demo-uid");
        List<PackagePurchase> myPurchases = packageService.getPackagePurchasesForTourist(currentUid);

        model.addAttribute("pkg", travelPackage);
        model.addAttribute("travelPackage", travelPackage);
        model.addAttribute("purchased", purchased);
        model.addAttribute("purchaseCount", purchaseCount);
        model.addAttribute("myPurchases", myPurchases);
        return "packages/detail";
    }

    @PostMapping("/packages/{id}/purchase")
    public String purchasePackage(
            @PathVariable String id,
            @RequestParam(required = false) String touristUid,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String effectiveUid = (touristUid != null && !touristUid.isBlank())
                ? touristUid
                : resolveSessionUid(session, "tourist-demo-uid");

        try {
            PackagePurchase purchase = packageService.purchasePackage(effectiveUid, id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Immutable package snapshot locked! Confirmation ID: " + purchase.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/packages/" + id;
        }
        return "redirect:/packages/" + id + "?purchased=true";
    }

    @GetMapping("/admin/packages")
    public String viewAdminPackages(Model model) {
        List<TravelPackage> packages = packageService.getPackages(false);
        List<Hotel> allHotels = hotelRepository.findAll();

        Map<String, Long> purchaseCounts = new HashMap<>();
        long totalPurchases = 0;
        long publishedCount = 0;
        for (TravelPackage pkg : packages) {
            long count = packagePurchaseRepository.countByPackageId(pkg.getId());
            purchaseCounts.put(pkg.getId(), count);
            totalPurchases += count;
            if (pkg.getStatus() == PackageStatus.PUBLISHED) {
                publishedCount++;
            }
        }

        model.addAttribute("packages", packages);
        model.addAttribute("hotels", allHotels);
        model.addAttribute("purchaseCounts", purchaseCounts);
        model.addAttribute("totalPackages", packages.size());
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("totalPurchases", totalPurchases);
        return "admin/packages";
    }

    @PostMapping("/admin/packages/create")
    public String createPackage(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Integer durationDays,
            @RequestParam BigDecimal priceTotal,
            @RequestParam(required = false, defaultValue = "LKR") String currency,
            @RequestParam(required = false) String coverImage,
            @RequestParam(required = false, defaultValue = "PUBLISHED") String status,
            @RequestParam("hotelIds") List<String> hotelIds,
            @RequestParam(value = "nights", required = false) List<Integer> nightsList,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String adminUid = resolveSessionUid(session, "admin-demo-uid");

        try {
            TravelPackage pkg = new TravelPackage();
            pkg.setTitle(title);
            pkg.setDescription(description);
            pkg.setDurationDays(durationDays);
            pkg.setPriceTotal(priceTotal);
            pkg.setCurrency(currency != null && !currency.isBlank() ? currency : "LKR");
            pkg.setCoverImage(coverImage != null && !coverImage.isBlank()
                    ? coverImage
                    : "https://images.unsplash.com/photo-1586861635167-e5223aadc9fe?auto=format&fit=crop&w=1200&q=80");
            pkg.setStatus(PackageStatus.valueOf(status.toUpperCase()));

            List<PackageHotel> stops = new ArrayList<>();
            if (hotelIds != null) {
                int order = 1;
                for (int i = 0; i < hotelIds.size(); i++) {
                    String hId = hotelIds.get(i);
                    if (hId == null || hId.isBlank()) {
                        continue;
                    }
                    int stopNights = (nightsList != null && i < nightsList.size() && nightsList.get(i) != null && nightsList.get(i) > 0)
                            ? nightsList.get(i)
                            : 2;
                    stops.add(new PackageHotel(hId, order++, stopNights));
                }
            }

            packageService.createPackage(adminUid, pkg, stops);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Curated multi-hotel travel package '" + title + "' created with " + stops.size() + " ordered stops.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/packages";
    }

    @PostMapping("/admin/packages/{id}/archive")
    public String archivePackage(
            @PathVariable String id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String adminUid = resolveSessionUid(session, "admin-demo-uid");
        try {
            packageService.archivePackage(adminUid, id);
            redirectAttributes.addFlashAttribute("successMessage", "Package archived.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    @GetMapping("/owner/packages")
    public String viewOwnerPackages(Model model) {
        return viewAdminPackages(model);
    }

    @PostMapping("/admin/packages/{id}/edit")
    public String editPackage(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Integer durationDays,
            @RequestParam BigDecimal priceTotal,
            @RequestParam(required = false, defaultValue = "PUBLISHED") String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String userUid = resolveSessionUid(session, "admin-demo-uid");
        try {
            packageService.updatePackage(userUid, id, title, description, durationDays, priceTotal, PackageStatus.valueOf(status.toUpperCase()));
            redirectAttributes.addFlashAttribute("successMessage", "Travel package '" + title + "' updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    @PostMapping({"/admin/packages/{id}/delete", "/owner/packages/{id}/delete"})
    public String deletePackage(
            @PathVariable String id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        return archivePackage(id, session, redirectAttributes);
    }

    private String resolveSessionUid(HttpSession session, String fallbackUid) {
        if (session != null) {
            Object uidAttr = session.getAttribute("uid");
            if (uidAttr instanceof String s && !s.isBlank()) {
                return s;
            }
            Object firebaseUidAttr = session.getAttribute("firebaseUid");
            if (firebaseUidAttr instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return fallbackUid;
    }
}
