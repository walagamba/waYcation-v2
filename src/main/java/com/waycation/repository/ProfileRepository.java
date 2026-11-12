package com.waycation.repository;

import com.waycation.model.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for querying and persisting user Profile entities.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {

    Optional<Profile> findByEmailIgnoreCase(String email);

    Optional<Profile> findByFirebaseUid(String firebaseUid);
}
