package com.example.blahblahapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_social_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String platform;
    private String url;
    private long createdAt;
    private long updatedAt;

    @PrePersist
    protected void onCreate(){
        long now = Instant.now().toEpochMilli();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = Instant.now().toEpochMilli();
    }
}
