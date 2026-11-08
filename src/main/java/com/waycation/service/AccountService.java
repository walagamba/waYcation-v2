package com.waycation.service;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.repository.ProfileRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final ProfileRepository profileRepository;

    public AccountService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public Profile authenticateUser(String email, String password, Role expectedRole) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        Profile profile = profileRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (expectedRole != null && profile.getRole() != expectedRole) {
            throw new SecurityException("Role mismatch for requested portal.");
        }
        return profile;
    }

    public Profile registerUserAccount(String displayName, String email, String password, Role role) {
        if (displayName == null || displayName.trim().length() < 2) {
            throw new IllegalArgumentException("Display name must be at least 2 characters.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (profileRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String generatedUid = "uid_" + UUID.randomUUID().toString().replace("-", "");
        Profile profile = new Profile(generatedUid, normalizedEmail, displayName.trim(), role);
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Profile getProfile(String firebaseUid) {
        return profileRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));
    }
}
