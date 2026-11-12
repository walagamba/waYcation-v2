package com.waycation.repository;

import com.waycation.model.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for querying and persisting user Profile entities.
 */
    // Spring Data JPA Repository interface for Profile data access
@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {

    // Derived query: generates SELECT SQL automatically by email
    Optional<Profile> findByEmailIgnoreCase(String email);

    // Derived query: lookup profile by unique UID identifier
    Optional<Profile> findByFirebaseUid(String firebaseUid);
}



