package com.waycation.actions;

import com.waycation.model.PackageHotel;
import com.waycation.model.PackagePurchase;
import com.waycation.model.TravelPackage;
import com.waycation.service.PackageService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Module 5: Admin Travel Package Management Actions
 * Provides server action entry points for creating multi-hotel travel packages,
 * archiving bundles, recording immutable tourist package purchases, and querying itineraries.
 */
@Component
    // Facade Design Pattern: wraps travel package workflows into clean action methods
public class PackageAction {

    private final PackageService packageService;

    public PackageAction(PackageService packageService) {
        this.packageService = packageService;
    }

    public TravelPackage createPackageAction(
            String adminUid,
            TravelPackage travelPackage,
            List<PackageHotel> stops
    ) {
        return packageService.createPackage(adminUid, travelPackage, stops);
    }

    public TravelPackage archivePackageAction(String adminUid, String packageId) {
        return packageService.archivePackage(adminUid, packageId);
    }

    public PackagePurchase purchasePackageAction(String touristUid, String packageId) {
        return packageService.purchasePackage(touristUid, packageId);
    }

    public List<TravelPackage> getAdminPackagesAction(String adminUid) {
        return packageService.getPackages(false);
    }

    public List<PackagePurchase> getTouristPackagePurchasesAction(String touristUid) {
        return packageService.getPackagePurchasesForTourist(touristUid);
    }
}

