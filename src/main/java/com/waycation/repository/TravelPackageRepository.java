package com.waycation.repository;

import com.waycation.model.PackageStatus;
import com.waycation.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

    // Spring Data JPA repository for package search and filtering
@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, String> {

    List<TravelPackage> findByStatusOrderByCreatedAtDesc(PackageStatus status);

    List<TravelPackage> findAllByOrderByCreatedAtDesc();
}

