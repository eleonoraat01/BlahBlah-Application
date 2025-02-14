package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.chat.ChatCreateDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.ChannelMember;
import com.example.blahblahapp.entities.Chat;
import com.example.blahblahapp.entities.Message;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.exceptions.AccessDeniedException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.repositories.ChatRepository;
import com.example.blahblahapp.repositories.FriendshipRepository;
import com.example.blahblahapp.repositories.MessageRepository;
import com.example.blahblahapp.repositories.UserRepository;
import com.example.blahblahapp.utils.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;

    public ChatDTO createChat(ChatCreateDTO createDTO, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        User user2 = userRepository.findById(createDTO.getRecipient())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if users are friends
        boolean areFriends = friendshipRepository.existsByUser_IdAndFriend_Id(
                currentUserId, user2.getId()
        ) || friendshipRepository.existsByUser_IdAndFriend_Id(
                user2.getId(), currentUserId
        );

        if (!areFriends) {
            throw new AccessDeniedException("Users must be friends to start a chat");
        }

        // Check if chat already exists
        Optional<Chat> existingChat = chatRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                currentUserId, user2.getId(),
                currentUserId, user2.getId()
        );

        if (existingChat.isPresent()) {
            List<Message> messages = messageRepository.findByChatIdAndIsActiveTrue(existingChat.get().getId());
            return DTOMapper.mapChatToDTO(existingChat.get(), messages, currentUserId);
        }

        Chat chat = Chat
                .builder()
                .user1(currentUser)
                .user2(user2)
                .isActive(true)
                .build();

        log.info("Chat created with user1 ID {} and user2 ID {}", currentUserId, user2.getId());

        chat = chatRepository.save(chat);
        return DTOMapper.mapChatToDTO(chat, List.of(), currentUserId);
    }

    public ChatDTO getChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        List<Message> messages = messageRepository.findByChatIdAndIsActiveTrue(chatId);
        return DTOMapper.mapChatToDTO(chat, messages, chat.getUser1().getId());
    }

    public List<ChatDTO> getUserChats(Long userId) {

        return chatRepository.findByUser1Id(userId)
                .stream()
                .map(chat -> {
                    List<Message> messages = messageRepository.findByChatIdAndIsActiveTrue(chat.getId());
                    return DTOMapper.mapChatToDTO(chat, messages, userId);
                })
                .collect(Collectors.toList());
    }
}
