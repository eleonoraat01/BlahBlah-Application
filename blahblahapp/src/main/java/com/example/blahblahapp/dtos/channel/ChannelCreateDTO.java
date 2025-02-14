package com.example.blahblahapp.dtos.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelCreateDTO {
    @NotBlank(message = "Channel name is required")
    @Size(min = 3, max = 50, message = "Channel name must be between 3 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
