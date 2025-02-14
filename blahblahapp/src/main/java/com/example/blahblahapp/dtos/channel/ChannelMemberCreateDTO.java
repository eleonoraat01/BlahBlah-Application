package com.example.blahblahapp.dtos.channel;

import com.example.blahblahapp.enums.ChannelMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMemberCreateDTO {
    @NotNull(message = "UserID is required")
    private Long userId;

    private ChannelMemberRole role = ChannelMemberRole.member; // By default
}
