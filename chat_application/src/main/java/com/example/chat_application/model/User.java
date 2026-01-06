package com.example.chat_application.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /* ================= EXISTING FIELDS (UNCHANGED) ================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 2048)
    private String publicKey;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Enumerated(EnumType.STRING)
    private Status status;

    /* ================= NEW PROFILE FIELDS (SAFE ADDITION) ================= */

    /**
     * Name shown in chat UI (Slack-style)
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Used later for audio/video calling
     */
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    /**
     * URL from S3 / Cloudinary / CDN
     */
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    /**
     * Short bio / about
     */
    @Column(length = 500)
    private String bio;

    /**
     * Profile setup gate
     * Default = false → forces first-time setup
     */
    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted = false;

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }

    /* ----------- NEW GETTERS / SETTERS ----------- */

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    /* ================= ENUM ================= */

    public enum Status {
        ONLINE,
        OFFLINE
    }

    /* ================= IMPORTANT: EQUALS & HASHCODE ================= */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
