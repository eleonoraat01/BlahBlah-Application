package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.message.MessageCreateDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.message.MessageUpdateDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.Channel;
import com.example.blahblahapp.entities.Chat;
import com.example.blahblahapp.entities.Message;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.exceptions.AccessDeniedException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.exceptions.ValidationException;
import com.example.blahblahapp.repositories.*;
import com.example.blahblahapp.utils.DTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final FriendshipRepository friendshipRepository;
    private final ObjectMapper objectMapper;

    public MessageDTO createChannelMessage(MessageCreateDTO messageDTO, Long senderId) {
        Channel channel = channelRepository
                .findById(messageDTO.getChannelId())
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found"));

        User sender = userRepository
                .findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), sender.getId())){
            throw new AccessDeniedException("User is not a member of this channel");
        }

        Message message = Message
                .builder()
                .channel(channel)
                .sender(sender)
                .content(messageDTO.getContent())
                .isSpecial(messageDTO.isSpecial())
                .isActive(true)
                .build();

        log.info("Message is created in channel with ID: {}", channel.getId());

        return DTOMapper.mapMessageToDTO(messageRepository.save(message));
    }

    public MessageDTO createChatMessage(MessageCreateDTO messageDTO, Long senderId) {

        User sender = userRepository
                .findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
            throw new ValidationException("Message content cannot be empty");
        }

        Chat chat;
        if (messageDTO.getChatId() != null) {
            // Use existing chat
            chat = chatRepository
                    .findById(messageDTO.getChatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

            // Check if sender is part of the chat
            if (!chat.getUser1().getId().equals(senderId) && !chat.getUser2().getId().equals(senderId)){
                throw new AccessDeniedException("User is not a member of this chat");
            }
        } else if (messageDTO.getRecipientId() != null) {
            // For new chat creation, verify recipient exists and friendship
            User recipientUser = userRepository.findById(messageDTO.getRecipientId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Check if users are friends
            boolean areFriends = friendshipRepository.existsByUser_IdAndFriend_Id(
                    senderId, recipientUser.getId()
            ) || friendshipRepository.existsByUser_IdAndFriend_Id(
                    recipientUser.getId(), senderId
            );

            if (!areFriends) {
                throw new AccessDeniedException("You can chat with friends only");
            }

            // Check for existing chat first
            chat = chatRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                            senderId, messageDTO.getRecipientId(),
                            senderId, messageDTO.getRecipientId())
                    .orElseGet(() -> {
                        Chat newChat = Chat
                                .builder()
                                .user1(sender)
                                .user2(recipientUser)
                                .isActive(true)
                                .build();
                        Chat savedChat = chatRepository.save(newChat);
                        return savedChat;
                    });
        } else {
            throw new AccessDeniedException("Either chatId or recipientId must be provided");
        }

        Message message = Message
                .builder()
                .chat(chat)
                .sender(sender)
                .content(messageDTO.getContent())
                .isSpecial(messageDTO.isSpecial())
                .isActive(true)
                .build();

        Message savedMessage = messageRepository.save(message);
        log.info("Message with ID: {} is created in chat with ID: {}", message.getId(), chat.getId());

        return DTOMapper.mapMessageToDTO(savedMessage);
    }

    public List<MessageDTO> getUserMessages(Long userId) {

        return messageRepository.findBySenderIdAndIsActiveTrue(userId)
                .stream()
                .map(DTOMapper::mapMessageToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO updateMessage(Long messageId, MessageUpdateDTO updateDTO, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(userId)) {
            throw new AccessDeniedException("Only message sender can update message");
        }

        if (updateDTO.getContent() == null || updateDTO.getContent().trim().isEmpty()) {
            throw new ValidationException("Message content cannot be empty");
        }

        message.setContent(updateDTO.getContent());
        message.setUpdatedAt(Instant.now().toEpochMilli());

        log.info("Updated message with new content: {}", updateDTO.getContent());

        return DTOMapper.mapMessageToDTO(messageRepository.save(message));
    }

    public List<MessageDTO> getChannelMessages(Long channelId, Long userId) {
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new AccessDeniedException("User is not a member of this channel");
        }

        return messageRepository
                .findByChannelIdAndIsActiveTrue(channelId)
                .stream()
                .map(DTOMapper::mapMessageToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepository
                .findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("User is not a participant in this chat");
        }

        return messageRepository
                .findByChatIdAndIsActiveTrue(chatId)
                .stream()
                .map(DTOMapper::mapMessageToDTO)
                .collect(Collectors.toList());
    }
}
