package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.channel.ChannelCreateDTO;
import com.example.blahblahapp.dtos.channel.ChannelDTO;
import com.example.blahblahapp.dtos.channel.ChannelMemberDTO;
import com.example.blahblahapp.dtos.channel.ChannelUpdateDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.*;
import com.example.blahblahapp.enums.ChannelMemberRole;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChannelDTO createChannel(ChannelCreateDTO createDTO, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (createDTO.getName() == null || createDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Channel name cannot be empty");
        }

        Channel channel = Channel.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .isActive(true)
                .build();

        channel = channelRepository.save(channel);
        log.info("Channel created with ID: {}", channel.getId());

        // Adding creator as a channel owner
        ChannelMember ownerMember = ChannelMember.builder()
                .channel(channel)
                .user(owner)
                .role(ChannelMemberRole.owner)
                .isActive(true)
                .build();

        channel.setMembers(List.of(ownerMember));

        ChannelMember savedMember = channelMemberRepository.save(ownerMember);
        log.info("Added owner as channel member with ID: {}", savedMember.getId());

        List<Message> messages = messageRepository.findByChannelIdAndIsActiveTrue(channel.getId());
        return DTOMapper.mapChannelToDTO(channel, messages);
    }

    public ChannelDTO updateChannel(Long channelId, ChannelUpdateDTO updateDTO, Long requesterId) {
        Channel channel = channelRepository
                .findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found"));

        // Check permissions
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("User is not a member of this channel"));

        if (requester.getRole() != ChannelMemberRole.owner &&
                requester.getRole() != ChannelMemberRole.admin) {
            throw new AccessDeniedException("Only owner and admins can update channel details");
        }

        if (updateDTO.getName() != null && updateDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Channel name cannot be empty");
        }

        // Update fields if provided
        if (updateDTO.getName() != null) {
            channel.setName(updateDTO.getName());
            log.info("Updating channel name of channel with ID: {} and new name {}",
                    channel.getId(), updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            channel.setDescription(updateDTO.getDescription());
            log.info("Updating channel description of channel with ID: {} and new name {}",
                    channel.getId(), updateDTO.getDescription());
        }

        channel = channelRepository.save(channel);
        List<Message> messages = messageRepository.findByChannelIdAndIsActiveTrue(channel.getId());
        return DTOMapper.mapChannelToDTO(channel, messages);
    }

    public void deleteChannel(Long channelId, Long requesterId) {
        Channel channel = channelRepository
                .findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        // Check if channel is already deleted
        if (!channel.isActive()) {
            throw new AccessDeniedException("Channel is already deleted");
        }

        // Check if requester is owner
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this channel"));

        if (requester.getRole() != ChannelMemberRole.owner){
            throw new AccessDeniedException("Only channel owner can delete the channel");
        }

        log.info("Deleting channel with ID: {}", channelId);

        channel.setActive(false);
        channelRepository.save(channel);
    }

    public ChannelDTO getChannelById(Long id) {
        Channel channel = channelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));

        List<Message> messages = messageRepository.findByChannelIdAndIsActiveTrue(channel.getId());
        return DTOMapper.mapChannelToDTO(channel, messages);
    }

    public List<ChannelDTO> getAllActiveChannels(){
        return channelRepository
                .findByIsActiveTrue()
                .stream()
                .map(channel -> {
                    List<Message> messages = messageRepository.findByChannelIdAndIsActiveTrue(channel.getId());
                    return DTOMapper.mapChannelToDTO(channel, messages);
                })
                .collect(Collectors.toList());
    }

    public List<ChannelDTO> getUserChannels(Long userId) {

        return channelMemberRepository
                .findByUserId(userId)
                .stream()
                .map(ChannelMember::getChannel)
                .map(channel -> {
                    List<Message> messages = messageRepository.findByChannelIdAndIsActiveTrue(channel.getId());
                    return DTOMapper.mapChannelToDTO(channel, messages);
                })
                .collect(Collectors.toList());
    }
}
