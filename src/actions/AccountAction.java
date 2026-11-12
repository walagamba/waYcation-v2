package com.waycation.actions;

import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Module 1: Account & Profile Management Actions
 * Provides server action entry points for user authentication, registration,
 * profile updates, compliant account anonymization, and administrator role management.
 */
@Component
    // Facade Design Pattern: wraps account operations into clean, reusable action methods
public class AccountAction {

    private final AccountService accountService;

    public AccountAction(AccountService accountService) {
        this.accountService = accountService;
    }

    public Profile getProfileAction(String firebaseUid) {
        return accountService.getProfile(firebaseUid);
    }

    public Profile updateProfileAction(String firebaseUid, String displayName, String avatarUrl) {
        return accountService.updateProfile(firebaseUid, displayName, avatarUrl);
    }

    public Profile requestAccountDeletionAction(String firebaseUid) {
        return accountService.requestAccountDeletion(firebaseUid);
    }

    public Profile signInAction(String email, String password, Role expectedRole) {
        return accountService.authenticateUser(email, password, expectedRole);
    }

    public Profile registerAction(String displayName, String email, String password, Role role) {
        return accountService.registerUserAccount(displayName, email, password, role);
    }

    public List<Profile> getAllUsersAction(String callerUid) {
        return accountService.getAllUsersForAdmin(callerUid);
    }

    public Profile updateUserRoleAction(String callerUid, String targetUserId, Role newRole) {
        return accountService.updateUserRoleByAdmin(callerUid, targetUserId, newRole);
    }
}

