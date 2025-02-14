package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.auth.AuthResponse;
import com.example.blahblahapp.dtos.auth.LoginRequest;
import com.example.blahblahapp.dtos.auth.RegisterRequest;
import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.enums.UserStatus;
import com.example.blahblahapp.exceptions.AuthenticationException;
import com.example.blahblahapp.exceptions.DuplicateResourceException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.repositories.UserRepository;
import com.example.blahblahapp.security.JwtService;
import com.example.blahblahapp.utils.DTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ChannelService channelService;
    private final UserService userService;
    private final ChatService chatService;
    private final DTOMapper dtoMapper;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update user status to online
        userService.updateUserStatus(user.getId(), UserStatus.online);
        return buildAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .displayname(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.online)
                .isActive(true)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    public void logout(String token) {
        // Get current user before clearing context
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(auth);
//        System.out.println(auth != null);
//        System.out.println(auth.getPrincipal() instanceof User);
//        System.out.println(auth.getPrincipal());
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            userService.updateUserStatus(user.getId(), UserStatus.offline);
            System.out.println("User logged out");
        }
        SecurityContextHolder.clearContext();
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        UserDTO userDTO = dtoMapper.mapFullUserToDTO(user);
        List<ChannelDTO> channels = channelService.getUserChannels(user.getId());
        List<ChatDTO> chats = chatService.getUserChats(user.getId());

        return AuthResponse
                .builder()
                .token(token)
                .user(userDTO)
                .channels(channels)
                .chats(chats)
                .build();
    }
}
