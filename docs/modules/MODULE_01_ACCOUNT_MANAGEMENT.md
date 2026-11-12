# Module 1: Account & Profile Management — Code & OOP Architecture

## 1. Module Overview
Module 1 manages user identity, role-based authorization (Tourist, Hotel Owner, Admin, Super Admin, Staff), user registration, credential authentication, profile management, and GDPR-compliant data anonymization.

---

## 2. Classes in This Module

| Class Name | Role / Layer | Package |
| :--- | :--- | :--- |
| **`Role`** | Enum / Domain Specification | `com.waycation.model` |
| **`Profile`** | JPA Entity Model | `com.waycation.model` |
| **`ProfileRepository`** | Data Access Layer (Spring Data JPA) | `com.waycation.repository` |
| **`AccountService`** | Business Logic & Transactional Service | `com.waycation.service` |
| **`AccountController`** | Presentation Layer / Web Controller | `com.waycation.controller` |
| **`AdminUserController`** | Presentation Layer / Admin User Governance | `com.waycation.controller` |
| **`AccountAction`** | Spring `@Component` Action Facade Layer | `com.waycation.actions` |

---

## 3. Attributes (Fields & Data Types)

### `Profile.java`:
- `private UUID id`: Primary key generated automatically via UUID (`@GeneratedValue(strategy = GenerationType.AUTO)`).
- `private String firebaseUid`: Unique user session identifier mapped to database column `firebase_uid` (`nullable = false, unique = true`).
- `private String email`: User email address (`nullable = false, unique = true`).
- `private String displayName`: Public user name (`name = "display_name"`).
- `private Role role`: Enumerated user authority (`@Enumerated(EnumType.STRING), nullable = false`).
- `private String avatarUrl`: Optional profile picture link (`name = "avatar_url"`).
- `private Instant createdAt`: Audit timestamp when record is inserted (`updatable = false`).
- `private Instant updatedAt`: Audit timestamp when record is modified.

### `Role.java`:
- Values: `TOURIST`, `HOTEL_OWNER`, `ADMIN`, `SUPER_ADMIN`, `STAFF`.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Encapsulation
- **Where**: In `Profile.java`, all attributes (`id`, `email`, `role`, etc.) are marked `private`.
- **Implementation**: Access is strictly regulated through `public` getters and setters. Sensitive internal states like `createdAt` and `updatedAt` have no public setters to prevent accidental tampering.

### B. Abstraction
- **Where**: `ProfileRepository.java` extends `JpaRepository<Profile, UUID>`.
- **Implementation**: The underlying SQL operations (`INSERT`, `SELECT`, `UPDATE`) and database connections are abstracted away behind simple method signatures (`findByEmailIgnoreCase`, `existsByEmailIgnoreCase`).

### C. Inheritance
- **Where**: `ProfileRepository` inherits generic CRUD functionality, pagination, and sorting from Spring Data JPA's `JpaRepository` and `PagingAndSortingRepository`.

### D. Polymorphism
- **Where**: In `AccountController` and `AccountService`, `Role` instances are evaluated polymorphically via method `isAdministrative()` to grant permission tiers dynamically without hardcoding role strings.

---

## 5. Constructors

- **No-Argument Constructor (`public Profile()`)**:
  - *Why*: Mandatory requirement by JPA/Hibernate to instantiate proxy entities via reflection during query deserialization.
- **Parameterized Constructor (`public Profile(String firebaseUid, String email, String displayName, Role role, String avatarUrl)`)**:
  - *Why*: Allows application code and `AccountService` to create fully initialized, immutable domain entities in a single line.

---

## 6. Getters & Setters (Why We Implement Them)
- **Why**:
  1. **Encapsulation**: Prevents external classes from directly altering internal object variables.
  2. **JavaBeans Standard**: Frameworks like Spring Boot, Thymeleaf (`th:text="${profile.displayName}"`), and Jackson JSON serializer rely on standard getter/setter patterns (`getDisplayName()`, `setDisplayName()`) to read and populate data.
  3. **Validation Guards**: Setters allow sanitization (e.g. trimming whitespace, checking nulls).

---

## 7. Key Methods & Business Logic

### In `AccountService.java`:
1. `canUserRegisterAsRole(Role role)`: Security boundary preventing self-registration into administrative tiers (`ADMIN`, `SUPER_ADMIN`).
2. `registerUserAccount(...)`: Enforces RFC 5322 email regex, minimum password lengths, duplicate email checks, and generates unique UIDs.
3. `authenticateUser(...)`: Verifies email and enforces portal access matching (e.g. tourists cannot access hotel owner portals).
4. `updateProfile(...)`: Null-safe update for display name and avatar URL.
5. `anonymizeAccount(String firebaseUid)`: Replaces user identifiers with anonymized placeholders to comply with GDPR data erasure while preserving relational foreign keys in booking audits.

---

## 8. CRUD Operations Mapping

| Operation | HTTP / Method | Service Method | Repository Method |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /sign-up` | `registerUserAccount(...)` | `profileRepository.save(profile)` |
| **Read** | `POST /sign-in`, `GET /account` | `authenticateUser(...)`, `getProfile(...)` | `findByEmailIgnoreCase(...)`, `findByFirebaseUid(...)` |
| **Update** | `POST /account/profile` | `updateProfile(...)` | `profileRepository.save(profile)` |
| **Delete (Soft)** | `POST /account/anonymize` | `anonymizeAccount(...)` | Anonymizes data & `profileRepository.save(profile)` |

---

## 9. Database Access Points & Relational Table
- **Target Relational Table**: `profiles`
- **Primary Key**: `id UUID PRIMARY KEY`
- **Unique Indexes**: `UNIQUE(email)`, `UNIQUE(firebase_uid)`
- **JPA Annotations**: `@Entity`, `@Table(name = "profiles")`, `@Id`, `@Column`, `@Enumerated(EnumType.STRING)`, `@PrePersist`, `@PreUpdate`.
