package com.example.blahblahapp.dtos.channel;

import com.example.blahblahapp.dtos.message.MessageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChannelDTO {
    private Long id;
    private String name;
    private String description;
    private List<ChannelMemberDTO> members;
    private List<MessageDTO> messages;

    @JsonProperty("isActive")
    private boolean isActive;

    private long createdAt;
    private long updatedAt;
}
