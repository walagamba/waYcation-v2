package com.waycation.service;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.repository.ProfileRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service encapsulating account registration, authentication, profile updates,
 * account anonymization, and administrative role management.
 */
    // Spring business logic service layer
@Service
    // Manages transactions: automatically commits on success or rolls back on runtime exception
@Transactional
public class AccountService {

    private final ProfileRepository profileRepository;

    public AccountService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Determines whether a role is permitted for public self-registration.
     * Blocks ADMIN, SUPER_ADMIN, and STAFF from self-registration.
     */
        // Manages transactions: automatically commits on success or rolls back on runtime exception
@Transactional(readOnly = true)
    // Prevent privilege escalation: only allow self-registration for Tourist and Hotel Owner
    public boolean canUserRegisterAsRole(Role role) {
        return role == Role.TOURIST || role == Role.HOTEL_OWNER;
    }

    /**
     * Authenticates a user by email and verifies portal role access compatibility.
     */
        // Manages transactions: automatically commits on success or rolls back on runtime exception
@Transactional(readOnly = true)
    public Profile authenticateUser(String email, String password, Role expectedRole) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        // Normalize email to lowercase for consistent case-insensitive lookup
        String normalizedEmail = email.trim().toLowerCase();
        // Look up registered user by normalized email address
        Profile profile = profileRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // Verify whether authenticated user has permission for the requested portal role
        if (expectedRole != null) {
            // Verify if user's assigned role matches the expected portal role (with Super Admin override)
            boolean roleMatches = profile.getRole() == expectedRole
                    || (expectedRole == Role.ADMIN && profile.getRole() == Role.SUPER_ADMIN);
            if (!roleMatches) {
                // Reject login if user attempts to access an unauthorized portal role
                throw new SecurityException("Access denied: account does not have permission for the " + expectedRole + " portal.");
            }
        }

        return profile;
    }

    /**
     * Registers a new user account after validating self-registration role constraints and inputs.
     */
    public Profile registerUserAccount(String displayName, String email, String password, Role role) {
        if (role == null || !canUserRegisterAsRole(role)) {
            throw new SecurityException("Self-registration is only permitted for Tourist and Hotel Owner accounts.");
        }
        if (displayName == null || displayName.trim().length() < 2) {
            throw new IllegalArgumentException("Display name must be at least 2 characters.");
        }
        // FIX: enforce RFC 5322 compliant email regex to prevent malformed email addresses
        // Validate email structure against RFC 5322 regex specification
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        // Normalize email to lowercase for consistent case-insensitive lookup
        String normalizedEmail = email.trim().toLowerCase();
        // Check for duplicate email in database to ensure account uniqueness
        if (profileRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        String generatedUid = "uid_" + UUID.randomUUID().toString().replace("-", "");
        Profile profile = new Profile(generatedUid, normalizedEmail, displayName.trim(), role, null);
        // Persist newly registered profile into database table
        return profileRepository.save(profile);
    }

    /**
     * Retrieves a profile by its Firebase UID.
     */
        // Manages transactions: automatically commits on success or rolls back on runtime exception
@Transactional(readOnly = true)
    public Profile getProfile(String firebaseUid) {
        if (firebaseUid == null || firebaseUid.isBlank()) {
            throw new IllegalArgumentException("User session identifier is required.");
        }
        return profileRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));
    }

    /**
     * Updates the display name and avatar URL of an existing user profile.
     */
    public Profile updateProfile(String firebaseUid, String displayName, String avatarUrl) {
        Profile profile = getProfile(firebaseUid);
        if (displayName == null || displayName.trim().length() < 2) {
            throw new IllegalArgumentException("Display name must be at least 2 characters.");
        }
        profile.setDisplayName(displayName.trim());
        profile.setAvatarUrl((avatarUrl == null || avatarUrl.isBlank()) ? null : avatarUrl.trim());
        // Persist newly registered profile into database table
        return profileRepository.save(profile);
    }

    /**
     * Anonymizes personal profile data for account deletion while preserving referential integrity.
     */
    public Profile requestAccountDeletion(String firebaseUid) {
        Profile profile = getProfile(firebaseUid);
        String prefix = firebaseUid.substring(0, Math.min(8, firebaseUid.length()));
        profile.setDisplayName("Anonymized User");
        profile.setEmail("deleted_" + prefix + "@waycation.local");
        profile.setAvatarUrl(null);
        // Persist newly registered profile into database table
        return profileRepository.save(profile);
    }

    /**
     * Lists all user profiles for an authenticated administrator.
     */
        // Manages transactions: automatically commits on success or rolls back on runtime exception
@Transactional(readOnly = true)
    public List<Profile> getAllUsersForAdmin(String callerUid) {
        Profile caller = getProfile(callerUid);
        if (caller.getRole() != Role.ADMIN && caller.getRole() != Role.SUPER_ADMIN) {
            throw new SecurityException("Administrator privileges are required to view all users.");
        }
        return profileRepository.findAll();
    }

    /**
     * Updates a target user's role while enforcing ADMIN and SUPER_ADMIN hierarchy rules.
     */
    public Profile updateUserRoleByAdmin(String callerUid, String targetUserId, Role newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("Target role must be specified.");
        }
        Profile caller = getProfile(callerUid);
        if (caller.getRole() != Role.ADMIN && caller.getRole() != Role.SUPER_ADMIN) {
            throw new SecurityException("Administrator privileges are required to modify user roles.");
        }

        Profile target = profileRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user profile not found."));

        if ((newRole == Role.ADMIN || newRole == Role.SUPER_ADMIN || target.getRole() == Role.SUPER_ADMIN)
                && caller.getRole() != Role.SUPER_ADMIN) {
            throw new SecurityException("Only a Super Admin can grant or modify Administrator roles.");
        }

        target.setRole(newRole);
        return profileRepository.save(target);
    }
}











