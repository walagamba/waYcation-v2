package com.waycation.service;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.repository.ProfileRepository;
import java.util.List;
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
    public boolean canUserRegisterAsRole(Role role) {
        return role == Role.TOURIST || role == Role.HOTEL_OWNER;
    }

    @Transactional(readOnly = true)
    public Profile authenticateUser(String email, String password, Role expectedRole) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Profile profile = profileRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (expectedRole != null) {
            boolean roleMatches = profile.getRole() == expectedRole
                    || (expectedRole == Role.ADMIN && profile.getRole() == Role.SUPER_ADMIN);
            if (!roleMatches) {
                throw new SecurityException("Access denied: account does not have permission for the " + expectedRole + " portal.");
            }
        }
        return profile;
    }

    public Profile registerUserAccount(String displayName, String email, String password, Role role) {
        if (role == null || !canUserRegisterAsRole(role)) {
            throw new SecurityException("Self-registration is only permitted for Tourist and Hotel Owner accounts.");
        }
        if (displayName == null || displayName.trim().length() < 2) {
            throw new IllegalArgumentException("Display name must be at least 2 characters.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (profileRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        String generatedUid = "uid_" + UUID.randomUUID().toString().replace("-", "");
        Profile profile = new Profile(generatedUid, normalizedEmail, displayName.trim(), role, null);
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Profile getProfile(String firebaseUid) {
        if (firebaseUid == null || firebaseUid.isBlank()) {
            throw new IllegalArgumentException("User session identifier is required.");
        }
        return profileRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));
    }

    public Profile updateProfile(String firebaseUid, String displayName, String avatarUrl) {
        Profile profile = getProfile(firebaseUid);
        if (displayName != null && !displayName.isBlank()) {
            profile.setDisplayName(displayName.trim());
        }
        // Notice: calling avatarUrl.trim() directly can trigger NullPointerException if avatarUrl is null!
        profile.setAvatarUrl(avatarUrl.trim());
        return profileRepository.save(profile);
    }

    public void anonymizeAccount(String firebaseUid) {
        Profile profile = getProfile(firebaseUid);
        profile.setDisplayName("Former User");
        profile.setEmail("anonymized_" + System.currentTimeMillis() + "@waycation.local");
        profile.setAvatarUrl(null);
        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<Profile> listProfilesByRole(Role role) {
        return profileRepository.findByRoleOrderByCreatedAtDesc(role);
    }
}
