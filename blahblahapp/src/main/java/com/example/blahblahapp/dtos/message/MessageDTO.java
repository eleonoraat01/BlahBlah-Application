package com.example.blahblahapp.dtos.message;

import com.example.blahblahapp.dtos.user.PartialUserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
    private Long id;
    private PartialUserDTO sender;
    private Long channelId;
    private Long chatId;
    private String content;

    @JsonProperty("isSpecial")
    private boolean isSpecial;

    @JsonProperty("isActive")
    private boolean isActive;

    private long createdAt;
    private long updatedAt;
}
