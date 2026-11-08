package com.waycation.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Profile() {}

    public Profile(String firebaseUid, String email, String displayName, Role role) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
