package com.waycation.repository;

import com.waycation.model.PackagePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagePurchaseRepository extends JpaRepository<PackagePurchase, String> {

    List<PackagePurchase> findByTouristUidOrderByCreatedAtDesc(String touristUid);

    long countByPackageId(String packageId);
}
