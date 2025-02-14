package com.example.blahblahapp.entities;

import com.example.blahblahapp.enums.ChannelMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "channel_members")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ChannelMemberRole role;

    @Column(name = "is_active")
    private boolean isActive;

    private long createdAt;
    private long updatedAt;

    @PrePersist
    protected void onCreate(){
        long now = Instant.now().toEpochMilli();
        createdAt = now;
        updatedAt = now;
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = Instant.now().toEpochMilli();
    }
}
