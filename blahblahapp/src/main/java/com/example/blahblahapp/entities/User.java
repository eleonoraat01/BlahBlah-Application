package com.example.blahblahapp.entities;

import com.example.blahblahapp.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String displayname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String phone;
    private String address;
    private String bio;
    private String profilePictureUrl;
    private String coverPictureUrl;

    @JsonProperty("isActive")
    @Column(name = "is_active")
    private Boolean isActive;

    private long createdAt;
    private long updatedAt;

    @OneToMany(mappedBy = "user")
    private List<UserInterest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserSocialLink> socialLinks;

    @PrePersist
    protected void onCreate() {
        long now = Instant.now().toEpochMilli();
        createdAt = now;
        updatedAt = now;
        isActive = true;
        this.status = UserStatus.online;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now().toEpochMilli();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }
}
