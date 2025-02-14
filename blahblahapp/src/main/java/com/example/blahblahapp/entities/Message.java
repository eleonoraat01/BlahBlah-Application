package com.example.blahblahapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_special")
    private boolean isSpecial;

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
