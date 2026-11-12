package com.waycation.actions;

import com.waycation.model.PackageHotel;
import com.waycation.model.PackagePurchase;
import com.waycation.model.TravelPackage;
import com.waycation.service.PackageService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Module 5: Curated Travel Package Management Actions
 * Provides server action entry points for creating, updating, archiving, and deleting
 * multi-hotel travel bundles, as well as recording tourist package purchases with custom start dates.
 */
@Component
public class PackageAction {

    private final PackageService packageService;

    public PackageAction(PackageService packageService) {
        this.packageService = packageService;
    }

    /**
     * Creates a curated multi-hotel travel package with ordered itinerary stops.
     */
    public TravelPackage createPackageAction(
            String adminUid,
            TravelPackage travelPackage,
            List<PackageHotel> stops
    ) {
        return packageService.createPackage(adminUid, travelPackage, stops);
    }

    /**
     * Updates an existing travel package's title, description, duration, price, and cover image.
     */
    public TravelPackage updatePackageAction(
            String adminUid,
            String packageId,
            TravelPackage updatedData
    ) {
        return packageService.updatePackage(adminUid, packageId, updatedData);
    }

    /**
     * Archives a travel package so it no longer appears in public discovery.
     */
    public TravelPackage archivePackageAction(String adminUid, String packageId) {
        return packageService.archivePackage(adminUid, packageId);
    }

    /**
     * Permanently deletes a travel package and its stop associations.
     */
    public void deletePackageAction(String adminUid, String packageId) {
        packageService.deletePackage(adminUid, packageId);
    }

    /**
     * Records a tourist package purchase using the default tomorrow start date.
     */
    public PackagePurchase purchasePackageAction(String touristUid, String packageId) {
        return packageService.purchasePackage(touristUid, packageId, null);
    }

    /**
     * Records a tourist package purchase with a tourist-selected itinerary start date.
     */
    public PackagePurchase purchasePackageAction(String touristUid, String packageId, LocalDate startDate) {
        return packageService.purchasePackage(touristUid, packageId, startDate);
    }

    /**
     * Retrieves all curated travel packages for the administrative console.
     */
    public List<TravelPackage> getAdminPackagesAction(String adminUid) {
        return packageService.getPackages(false);
    }

    /**
     * Retrieves all package purchases placed by a specific tourist.
     */
    public List<PackagePurchase> getTouristPackagePurchasesAction(String touristUid) {
        return packageService.getPackagePurchasesForTourist(touristUid);
    }
}
