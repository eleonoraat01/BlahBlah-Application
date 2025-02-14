package com.example.blahblahapp.utils;

import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.channel.ChannelMemberDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.user.*;
import com.example.blahblahapp.entities.*;
import com.example.blahblahapp.repositories.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DTOMapper {
    private final FriendshipRepository friendshipRepository;

    // Full User mapping for main user objects
    public UserDTO mapFullUserToDTO(User user) {
        if (user == null) return null;

        List<Friendship> friendships = friendshipRepository.findFriendsByUserId(user.getId());
        List<FriendshipDTO> friendshipDTOs = friendships.stream()
                .map(friendship -> DTOMapper.mapFriendshipToDTO(friendship, user.getId()))
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayname(user.getDisplayname())
                .email(user.getEmail())
                .status(user.getStatus())
                .phone(user.getPhone())
                .address(user.getAddress())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .coverPictureUrl(user.getCoverPictureUrl())
                .interests(Optional.ofNullable(user.getInterests())
                        .orElse(List.of())
                        .stream()
                        .map(DTOMapper::mapInterestToDTO)
                        .collect(Collectors.toList()))
                .socialLinks(Optional.ofNullable(user.getSocialLinks())
                        .orElse(List.of())
                        .stream()
                        .map(DTOMapper::mapSocialLinkToDTO)
                        .collect(Collectors.toList()))
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .friendships(friendshipDTOs)
                .build();
    }

    // Simplified User mapping for nested objects
    public static PartialUserDTO mapNestedUserToDTO(User user) {

        if (user == null) return null;
        return PartialUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayname(user.getDisplayname())
                .status(user.getStatus())
                .profilePictureUrl(user.getProfilePictureUrl())
//                .isActive(user.getIsActive())
//                .createdAt(user.getCreatedAt())
//                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static ChannelMemberDTO mapChannelMemberToDTO(ChannelMember member) {
        if (member == null) return null;
        return ChannelMemberDTO.builder()
                .id(member.getId())
                .user(mapNestedUserToDTO(member.getUser()))
                .role(member.getRole().toString())
                .joinedAt(member.getCreatedAt())
                .isActive(member.isActive())
                .build();
    }

    public static MessageDTO mapMessageToDTO(Message message) {
        if (message == null) return null;
        return MessageDTO.builder()
                .id(message.getId())
                .sender(mapNestedUserToDTO(message.getSender()))
                .channelId(message.getChannel() != null ? message.getChannel().getId() : null)
                .chatId(message.getChat() != null ? message.getChat().getId() : null)
                .content(message.getContent())
                .isSpecial(message.isSpecial())
                .isActive(message.isActive())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    public static ChannelDTO mapChannelToDTO(Channel channel, List<Message> messages) {
        if (channel == null) return null;
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .members(Optional.ofNullable(channel.getMembers())
                        .orElse(List.of())
                        .stream()
                        .map(DTOMapper::mapChannelMemberToDTO)
                        .collect(Collectors.toList()))
                .messages(Optional.ofNullable(messages)
                        .orElse(List.of())
                        .stream()
                        .map(DTOMapper::mapMessageToDTO)
                        .collect(Collectors.toList()))
                .isActive(channel.isActive())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }

    public static ChatDTO mapChatToDTO(Chat chat, List<Message> messages, Long currentUserId) {
        if (chat == null) return null;

        User user1 = Objects.equals(chat.getUser1().getId(), currentUserId) ? chat.getUser1() : chat.getUser2();
        User user2 = Objects.equals(chat.getUser1().getId(), currentUserId) ? chat.getUser2() : chat.getUser1();

        return ChatDTO.builder()
                .id(chat.getId())
                .user1(mapNestedUserToDTO(user1))
                .user2(mapNestedUserToDTO(user2))
                .messages(Optional.ofNullable(messages)
                        .orElse(List.of())
                        .stream()
                        .map(DTOMapper::mapMessageToDTO)
                        .collect(Collectors.toList()))
                .isActive(chat.isActive())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .build();
    }

    public static FriendshipDTO mapFriendshipToDTO(Friendship friendship, Long currentUserId) {
        if (friendship == null) return null;

        User user = Objects.equals(friendship.getUser().getId(), currentUserId) ? friendship.getUser() : friendship.getFriend();
        User friend = Objects.equals(friendship.getUser().getId(), currentUserId) ? friendship.getFriend() : friendship.getUser();

        return FriendshipDTO.builder()
                .id(friendship.getId())
                .user(mapNestedUserToDTO(user))
                .friend(mapNestedUserToDTO(friend))
                .createdAt(friendship.getCreatedAt())
                .updatedAt(friendship.getUpdatedAt())
                .build();
    }

    private static InterestDTO mapInterestToDTO(UserInterest interest) {
        return new InterestDTO(interest.getInterest());
    }

    private static SocialLinkDTO mapSocialLinkToDTO(UserSocialLink link) {
        return new SocialLinkDTO(link.getPlatform(), link.getUrl());
    }
}
