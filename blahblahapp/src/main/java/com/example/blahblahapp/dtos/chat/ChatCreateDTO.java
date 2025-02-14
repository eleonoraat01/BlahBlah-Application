package com.example.blahblahapp.dtos.chat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreateDTO {
    @NotNull(message = "Recipient is required")
    private Long recipient; // Target user for chatting (user1 is current user)
}
