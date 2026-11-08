package com.waycation.repository;

import com.waycation.model.Profile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByFirebaseUid(String firebaseUid);
    Optional<Profile> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
