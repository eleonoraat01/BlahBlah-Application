package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.user.*;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.entities.UserInterest;
import com.example.blahblahapp.entities.UserSocialLink;
import com.example.blahblahapp.enums.UserStatus;
import com.example.blahblahapp.exceptions.DuplicateResourceException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.repositories.FriendshipRepository;
import com.example.blahblahapp.repositories.UserInterestRepository;
import com.example.blahblahapp.repositories.UserRepository;
import com.example.blahblahapp.repositories.UserSocialLinkRepository;
import com.example.blahblahapp.utils.DTOMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserSocialLinkRepository userSocialLinkRepository;
    private final ObjectMapper objectMapper;
    private final FriendshipService friendshipService;
    private final DTOMapper dtoMapper;

    public UserDTO createUser(UserCreateDTO userDTO) {
        User user = User
                .builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .isActive(true)
                .createdAt(Instant.now().toEpochMilli())
                .build();

        return dtoMapper.mapFullUserToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update fields if they are not null
        Optional.ofNullable(updateDTO.getDisplayname())
                .ifPresent(user::setDisplayname);

        Optional.ofNullable(updateDTO.getEmail())
                .ifPresent(email -> {
                    // Check if email is already taken by another user
                    userRepository.findByEmail(email)
                            .filter(u -> !u.getId().equals(userId))
                            .ifPresent(u -> {
                                throw new DuplicateResourceException("Email already in use");
                            });
                    user.setEmail(email);
                });

        Optional.ofNullable(updateDTO.getStatus())
                .ifPresent(user::setStatus);

        Optional.ofNullable(updateDTO.getPhone())
                .ifPresent(user::setPhone);

        Optional.ofNullable(updateDTO.getAddress())
                .ifPresent(user::setAddress);

        Optional.ofNullable(updateDTO.getBio())
                .ifPresent(user::setBio);

        Optional.ofNullable(updateDTO.getProfilePictureUrl())
                .ifPresent(user::setProfilePictureUrl);

        Optional.ofNullable(updateDTO.getCoverPictureUrl())
                .ifPresent(user::setCoverPictureUrl);

        // Update interests if provided
        if (updateDTO.getInterests() != null) {
            updateUserInterests(user, updateDTO.getInterests());
        }

        // Update social links if provided
        if (updateDTO.getSocialLinks() != null) {
            updateUserSocialLinks(user, updateDTO.getSocialLinks());
        }

        log.info("Updated info for user with ID: {}", userId);

        return dtoMapper.mapFullUserToDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("Deleted user with ID: {}", user.getId());
    }

    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
        log.info("Updated user status with ID: {} and new status: {}", user.getId(), status);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return dtoMapper.mapFullUserToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(dtoMapper::mapFullUserToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query)
                .stream()
                .map(dtoMapper::mapFullUserToDTO)
                .collect(Collectors.toList());
    }

    private void updateUserInterests(User user, List<InterestDTO> newInterests) {
        // Remove existing interests
        List<UserInterest> existingInterests = userInterestRepository.findByUserId(user.getId());
        userInterestRepository.deleteAll(existingInterests);

        // Add new interests
        List<UserInterest> interests = newInterests.stream()
                .map(dto -> UserInterest.builder()
                        .user(user)
                        .interest(dto.getInterest())
                        .build())
                .collect(Collectors.toList());

        userInterestRepository.saveAll(interests);
        user.setInterests(interests);
    }

    private void updateUserSocialLinks(User user, List<SocialLinkDTO> newLinks) {
        // Remove existing social links
        List<UserSocialLink> existingLinks = userSocialLinkRepository.findByUserId(user.getId());
        userSocialLinkRepository.deleteAll(existingLinks);

        // Add new social links
        List<UserSocialLink> links = newLinks.stream()
                .map(dto -> UserSocialLink.builder()
                        .user(user)
                        .platform(dto.getPlatform())
                        .url(dto.getUrl())
                        .build())
                .collect(Collectors.toList());

        userSocialLinkRepository.saveAll(links);
        user.setSocialLinks(links);
    }
}
