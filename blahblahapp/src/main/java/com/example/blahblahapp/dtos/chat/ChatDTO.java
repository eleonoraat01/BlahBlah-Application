package com.example.blahblahapp.dtos.chat;

import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.user.PartialUserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    private Long id;
    private PartialUserDTO user1;
    private PartialUserDTO user2;

    @JsonProperty("isActive")
    private boolean isActive;

    private List<MessageDTO> messages;
    private long createdAt;
    private long updatedAt;
}
