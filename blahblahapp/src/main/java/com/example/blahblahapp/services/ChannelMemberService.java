package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.channel.ChannelMemberCreateDTO;
import com.example.blahblahapp.dtos.channel.ChannelMemberDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.Channel;
import com.example.blahblahapp.entities.ChannelMember;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.enums.ChannelMemberRole;
import com.example.blahblahapp.exceptions.AccessDeniedException;
import com.example.blahblahapp.exceptions.MemberAlreadyExistsException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.repositories.*;
import com.example.blahblahapp.utils.DTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public ChannelMemberDTO addMember(Long channelId, ChannelMemberCreateDTO createDTO, Long requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found"));

        // Check if requester is owner or admin
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this channel"));

        if (!requester.isActive()) {
            throw new AccessDeniedException("Your membership is not active");
        }

        if (requester.getRole() != ChannelMemberRole.owner &&
                requester.getRole() != ChannelMemberRole.admin) {
            throw new AccessDeniedException("Only the owner and admins can add members");
        }

        User newMember = userRepository
                .findById(createDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is already an active member
        if (channelMemberRepository.existsByChannelIdAndUserIdAndIsActiveTrue(channelId, createDTO.getUserId())) {
            throw new MemberAlreadyExistsException("User is already an active member of this channel");
        }

        // If not active, check for any existing membership to reactivate
        Optional<ChannelMember> existingMember = channelMemberRepository
                .findByChannelIdAndUserId(channelId, createDTO.getUserId());

        if (existingMember.isPresent()) {
            ChannelMember member = existingMember.get();
            member.setActive(true);
            member.setRole(ChannelMemberRole.member);
            return DTOMapper.mapChannelMemberToDTO(channelMemberRepository.save(member));
        }

        // Create new membership if no existing membership found
        ChannelMember newMembership = ChannelMember.builder()
                .channel(channel)
                .user(newMember)
                .role(ChannelMemberRole.member)
                .isActive(true)
                .build();

        log.info("Channel member added with ID: {}", newMembership.getId());

        return DTOMapper.mapChannelMemberToDTO(channelMemberRepository.save(newMembership));
    }

    public void removeMember(Long channelId, Long userId, Long requesterId) {
        // Check if requester is owner
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this channel"));

        if (requester.getRole() != ChannelMemberRole.owner) {
            throw new AccessDeniedException("Only the owner can remove members");
        }

        // Find target active member
        ChannelMember targetMember = channelMemberRepository
                .findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found or already removed"));

        // Prevent removing the owner
        if (targetMember.getRole() == ChannelMemberRole.owner) {
            throw new IllegalStateException("Cannot remove the channel owner");
        }

        log.info("Removing channel member with ID: {}", targetMember.getId());

        targetMember.setActive(false);
//        channelMemberRepository.save(targetMember); --> changed from soft delete to hard delete, but not in the DB
        channelMemberRepository.delete(targetMember);
    }

    public ChannelMemberDTO promoteToAdminRole(Long channelId, Long targetUserId, Long requesterId) {
        // Check if requester is owner
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this channel"));

        if (requester.getRole() != ChannelMemberRole.owner) {
            throw new AccessDeniedException("Only the owners can promote members to admins");
        }

        // Find the target member and update role
        ChannelMember targetMember = channelMemberRepository
                .findByChannelIdAndUserId(channelId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // Prevent promoting if target is already an admin
        if (targetMember.getRole() == ChannelMemberRole.admin) {
            throw new IllegalStateException("User is already an admin");
        }

        // Prevent promoting the owner
        if (targetMember.getRole() == ChannelMemberRole.owner) {
            throw new IllegalStateException("Cannot modify the owner's role");
        }

        log.info("Promoting channel member to admin role. Admin ID: {}", targetMember.getId());

        targetMember.setRole(ChannelMemberRole.admin);
        return DTOMapper.mapChannelMemberToDTO(channelMemberRepository.save(targetMember));
    }

    public ChannelMemberDTO demoteFromAdminRole(Long channelId, Long targetUserId, Long requesterId) {
        // Check if requester is owner
        ChannelMember requester = channelMemberRepository
                .findByChannelIdAndUserId(channelId, requesterId)
                .orElseThrow(() -> new AccessDeniedException("Not a member of this channel"));

        if (requester.getRole() != ChannelMemberRole.owner) {
            throw new AccessDeniedException("Only the owner can demote admins");
        }

        // Find the target member and verify they are already admin
        ChannelMember targetMember = channelMemberRepository
                .findByChannelIdAndUserId(channelId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (targetMember.getRole() != ChannelMemberRole.admin) {
            throw new IllegalStateException("This user is not an admin");
        }

        // Prevent demoting the owner
        if (targetMember.getRole() == ChannelMemberRole.owner) {
            throw new IllegalStateException("Cannot modify the owner's role");
        }

        log.info("Demoting channel admin to member. Member ID: {}", targetMember.getId());

        targetMember.setRole(ChannelMemberRole.member);
        return DTOMapper.mapChannelMemberToDTO(channelMemberRepository.save(targetMember));
    }

    public List<ChannelMemberDTO> getChannelMembers(Long channelId) {
        return channelMemberRepository
                .findByChannelIdAndIsActiveTrue(channelId)
                .stream()
                .map(DTOMapper::mapChannelMemberToDTO)
                .collect(Collectors.toList());
    }
}
