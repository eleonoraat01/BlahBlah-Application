package com.example.blahblahapp.dtos.auth;

import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserDTO user;
    private List<ChannelDTO> channels;
    private List<ChatDTO> chats;
}
