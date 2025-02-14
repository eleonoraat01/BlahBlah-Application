package com.example.blahblahapp.dtos.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class MessageCreateDTO {
    private Long channelId;
    private Long chatId;
    private Long recipientId;

    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;

    @JsonProperty("isSpecial")
    private boolean isSpecial;
}
