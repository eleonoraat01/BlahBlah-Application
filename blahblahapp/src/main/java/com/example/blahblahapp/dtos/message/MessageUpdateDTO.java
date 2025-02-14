package com.example.blahblahapp.dtos.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageUpdateDTO {
    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;
}
