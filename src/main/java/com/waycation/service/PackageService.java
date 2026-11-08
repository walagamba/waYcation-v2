package com.waycation.service;

import com.waycation.model.PackageStatus;
import com.waycation.model.TravelPackage;
import com.waycation.repository.TravelPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PackageService {

    private final TravelPackageRepository packageRepository;

    public PackageService(TravelPackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public TravelPackage createPackage(String adminUid, TravelPackage pkg) {
        pkg.setCreatedByUid(adminUid);
        pkg.setStatus(PackageStatus.PUBLISHED);
        return packageRepository.save(pkg);
    }

    public List<TravelPackage> getPackages(boolean publishedOnly) {
        return packageRepository.findAll();
    }
}
