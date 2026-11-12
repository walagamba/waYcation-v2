package com.waycation.service;

import com.waycation.model.Hotel;
import com.waycation.model.PackageHotel;
import com.waycation.model.PackagePurchase;
import com.waycation.model.PackageStatus;
import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.model.TravelPackage;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.PackagePurchaseRepository;
import com.waycation.repository.ProfileRepository;
import com.waycation.repository.TravelPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Service layer managing curated package creation, partner hotel mapping, and purchases
@Service
public class PackageService {

    private final TravelPackageRepository travelPackageRepository;
    private final PackagePurchaseRepository packagePurchaseRepository;
    private final HotelRepository hotelRepository;
    private final ProfileRepository profileRepository;

    public PackageService(
            TravelPackageRepository travelPackageRepository,
            PackagePurchaseRepository packagePurchaseRepository,
            HotelRepository hotelRepository,
            ProfileRepository profileRepository) {
        this.travelPackageRepository = travelPackageRepository;
        this.packagePurchaseRepository = packagePurchaseRepository;
        this.hotelRepository = hotelRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public TravelPackage createPackage(String adminUid, TravelPackage pkg, List<PackageHotel> stops) {
        verifyAdminAccess(adminUid);

        if (stops == null || stops.size() < 2) {
            throw new IllegalArgumentException("A curated travel package must include at least 2 hotel stops.");
        }

        if (pkg.getTitle() == null || pkg.getTitle().isBlank()) {
            throw new IllegalArgumentException("Package title is required.");
        }
        if (pkg.getDurationDays() == null || pkg.getDurationDays() < 1) {
            throw new IllegalArgumentException("Package duration must be at least 1 day.");
        }
        if (pkg.getPriceTotal() == null || pkg.getPriceTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Package bundle price must be greater than zero.");
        }

        pkg.setCreatedByUid(adminUid);
        if (pkg.getStatus() == null) {
            // Set vacation package status to PUBLISHED for catalog listing
            pkg.setStatus(PackageStatus.PUBLISHED);
        }
        if (pkg.getCurrency() == null || pkg.getCurrency().isBlank()) {
            pkg.setCurrency("LKR");
        }

        Set<String> seenHotelIds = new HashSet<>();
        List<PackageHotel> linkedStops = new ArrayList<>();
        int index = 1;
        for (PackageHotel stop : stops) {
            if (stop.getHotelId() == null || stop.getHotelId().isBlank()) {
                continue;
            }
            if (!seenHotelIds.add(stop.getHotelId().trim())) {
                throw new IllegalArgumentException("Each stop in a curated package must be a distinct hotel property. Duplicate hotel was selected.");
            }
            stop.setTravelPackage(pkg);
            if (stop.getStopOrder() <= 0) {
                stop.setStopOrder(index);
            }
            if (stop.getNights() <= 0) {
                stop.setNights(1);
            }
            linkedStops.add(stop);
            index++;
        }

        if (linkedStops.size() < 2) {
            throw new IllegalArgumentException("A curated travel package must include at least 2 distinct hotel stops.");
        }

        linkedStops.sort(Comparator.comparingInt(PackageHotel::getStopOrder));
        pkg.setHotels(linkedStops);

        // Persist new travel package entity to database
        TravelPackage saved = travelPackageRepository.save(pkg);
        enrichPackageStops(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<TravelPackage> getPackages(boolean publishedOnly) {
        List<TravelPackage> packages = publishedOnly
                ? travelPackageRepository.findByStatusOrderByCreatedAtDesc(PackageStatus.PUBLISHED)
                : travelPackageRepository.findAllByOrderByCreatedAtDesc();

        for (TravelPackage pkg : packages) {
            enrichPackageStops(pkg);
        }
        return packages;
    }

    @Transactional(readOnly = true)
    public TravelPackage getPackageById(String id) {
        TravelPackage pkg = travelPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Travel package not found: " + id));
        enrichPackageStops(pkg);
        return pkg;
    }

    @Transactional
    public PackagePurchase purchasePackage(String touristUid, String packageId) {
        return purchasePackage(touristUid, packageId, java.time.LocalDate.now().plusDays(7));
    }

    @Transactional
    public PackagePurchase purchasePackage(String touristUid, String packageId, java.time.LocalDate startDate) {
        if (touristUid == null || touristUid.isBlank()) {
            throw new IllegalArgumentException("Tourist authentication is required to purchase a travel package.");
        }

        TravelPackage pkg = getPackageById(packageId);
        if (pkg.getStatus() != PackageStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED travel packages can be purchased.");
        }

        String snapshotJson = buildPackageSnapshotJson(pkg);

        // Instantiate purchase record for tourist package order
        PackagePurchase purchase = new PackagePurchase();
        purchase.setPackageId(pkg.getId());
        purchase.setTouristUid(touristUid);
        purchase.setPriceSnapshot(pkg.getPriceTotal());
        purchase.setCurrency(pkg.getCurrency() != null ? pkg.getCurrency() : "LKR");
        purchase.setPackageSnapshot(snapshotJson);
        purchase.setStatus("CONFIRMED");
        purchase.setCreatedAt(LocalDateTime.now());
        purchase.setStartDate(startDate != null ? startDate : java.time.LocalDate.now().plusDays(7));

        return packagePurchaseRepository.save(purchase);
    }

    @Transactional
    public TravelPackage archivePackage(String adminUid, String packageId) {
        verifyAdminAccess(adminUid);
        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Travel package not found: " + packageId));
        pkg.setStatus(PackageStatus.ARCHIVED);
        pkg.setUpdatedAt(LocalDateTime.now());
        // Persist new travel package entity to database
        TravelPackage saved = travelPackageRepository.save(pkg);
        enrichPackageStops(saved);
        return saved;
    }

    @Transactional
    public TravelPackage updatePackage(String adminUid, String packageId, TravelPackage updatedData) {
        if (updatedData == null) {
            throw new IllegalArgumentException("Updated package data cannot be null.");
        }
        return updatePackage(
                adminUid,
                packageId,
                updatedData.getTitle(),
                updatedData.getDescription(),
                updatedData.getDurationDays(),
                updatedData.getPriceTotal(),
                updatedData.getStatus()
        );
    }

    @Transactional
    public TravelPackage updatePackage(
            String adminUid,
            String packageId,
            String title,
            String description,
            Integer durationDays,
            BigDecimal priceTotal,
            PackageStatus status) {
        verifyAdminAccess(adminUid);
        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Travel package not found: " + packageId));

        if (title != null && !title.isBlank()) {
            pkg.setTitle(title);
        }
        if (description != null) {
            pkg.setDescription(description);
        }
        if (durationDays != null && durationDays > 0) {
            pkg.setDurationDays(durationDays);
        }
        if (priceTotal != null && priceTotal.compareTo(BigDecimal.ZERO) > 0) {
            pkg.setPriceTotal(priceTotal);
        }
        if (status != null) {
            pkg.setStatus(status);
        }
        pkg.setUpdatedAt(LocalDateTime.now());

        // Persist new travel package entity to database
        TravelPackage saved = travelPackageRepository.save(pkg);
        enrichPackageStops(saved);
        return saved;
    }

    @Transactional
    public void deletePackage(String adminUid, String packageId) {
        verifyAdminAccess(adminUid);
        TravelPackage pkg = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Travel package not found: " + packageId));
        travelPackageRepository.delete(pkg);
    }

    @Transactional(readOnly = true)
    public List<PackagePurchase> getPackagePurchasesForTourist(String touristUid) {
        return packagePurchaseRepository.findByTouristUidOrderByCreatedAtDesc(touristUid);
    }

    private void verifyAdminAccess(String adminUid) {
        if (adminUid == null || adminUid.isBlank()) {
            throw new SecurityException("Administrator UID is required.");
        }
        Optional<Profile> profileOpt = profileRepository.findByFirebaseUid(adminUid);
        if (profileOpt.isEmpty()) {
            profileOpt = profileRepository.findById(adminUid);
        }
        if (profileOpt.isPresent()) {
            Role role = profileOpt.get().getRole();
            if (role != Role.ADMIN && role != Role.SUPER_ADMIN) {
                throw new SecurityException("Only ADMIN or SUPER_ADMIN users can manage travel packages.");
            }
        }
    }

    private void enrichPackageStops(TravelPackage pkg) {
        if (pkg.getHotels() == null) {
            return;
        }
        for (PackageHotel stop : pkg.getHotels()) {
            if (stop.getHotelId() != null) {
                Optional<Hotel> hotelOpt = hotelRepository.findById(stop.getHotelId());
                if (hotelOpt.isPresent()) {
                    Hotel hotel = hotelOpt.get();
                    stop.setHotelName(hotel.getName());
                    stop.setCity(hotel.getCity());
                } else {
                    if (stop.getHotelName() == null || stop.getHotelName().isBlank()) {
                        stop.setHotelName("Curated Resort (" + stop.getHotelId() + ")");
                    }
                    if (stop.getCity() == null || stop.getCity().isBlank()) {
                        stop.setCity("Sri Lanka");
                    }
                }
            }
        }
    }

    private String buildPackageSnapshotJson(TravelPackage pkg) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"packageId\":\"").append(escapeJson(pkg.getId())).append("\",");
        sb.append("\"title\":\"").append(escapeJson(pkg.getTitle())).append("\",");
        sb.append("\"durationDays\":").append(pkg.getDurationDays() != null ? pkg.getDurationDays() : 0).append(",");
        sb.append("\"priceTotal\":").append(pkg.getPriceTotal() != null ? pkg.getPriceTotal().toPlainString() : "0").append(",");
        sb.append("\"currency\":\"").append(escapeJson(pkg.getCurrency() != null ? pkg.getCurrency() : "LKR")).append("\",");
        sb.append("\"stops\":[");

        List<PackageHotel> stops = pkg.getHotels() != null ? pkg.getHotels() : List.of();
        for (int i = 0; i < stops.size(); i++) {
            PackageHotel stop = stops.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"stopOrder\":").append(stop.getStopOrder()).append(",");
            sb.append("\"hotelId\":\"").append(escapeJson(stop.getHotelId())).append("\",");
            sb.append("\"hotelName\":\"").append(escapeJson(stop.getHotelName() != null ? stop.getHotelName() : "")).append("\",");
            sb.append("\"city\":\"").append(escapeJson(stop.getCity() != null ? stop.getCity() : "")).append("\",");
            sb.append("\"nights\":").append(stop.getNights());
            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
