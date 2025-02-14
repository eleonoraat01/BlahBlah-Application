package com.example.blahblahapp.controllers;

import com.example.blahblahapp.dtos.channel.*;
import com.example.blahblahapp.dtos.message.MessageCreateDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.services.ChannelMemberService;
import com.example.blahblahapp.services.ChannelService;
import com.example.blahblahapp.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ChannelMemberService channelMemberService;

    @PostMapping
    public ResponseEntity<ChannelDTO> createChannel(
            @Valid @RequestBody ChannelCreateDTO request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.createChannel(request, currentUser.getId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChannelDTO>> getUserChannels(@PathVariable Long userId) {
        return ResponseEntity.ok(channelService.getUserChannels(userId));
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDTO> updateChannel(
            @PathVariable Long channelId,
            @Valid @RequestBody ChannelUpdateDTO updateDTO,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(channelService.updateChannel(channelId, updateDTO, currentUser.getId()));
    }

    @PostMapping("/{channelId}/members")
    public ResponseEntity<ChannelMemberDTO> addMember(
            @PathVariable Long channelId,
            @Valid @RequestBody ChannelMemberCreateDTO channelMemberCreateDTO,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(channelMemberService.addMember(channelId, channelMemberCreateDTO, currentUser.getId()));
    }

    @PatchMapping("/{channelId}/members/{userId}/promote")
    public ResponseEntity<ChannelMemberDTO> promoteToAdminRole(
            @PathVariable Long channelId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(channelMemberService.promoteToAdminRole(channelId, userId, currentUser.getId()));
    }

    @PatchMapping("/{channelId}/members/{userId}/demote")
    public ResponseEntity<ChannelMemberDTO> demoteFromAdminRole(
            @PathVariable Long channelId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(channelMemberService.demoteFromAdminRole(channelId, userId, currentUser.getId()));
    }

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<MessageDTO> addMessage(
            @PathVariable Long channelId,
            @Valid @RequestBody MessageCreateDTO request,
            @AuthenticationPrincipal User currentUser) {
        request.setChannelId(channelId);
        return ResponseEntity.ok(messageService.createChannelMessage(request, currentUser.getId()));
    }

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<MessageDTO>> getChannelMessages(
            @PathVariable Long channelId,
            @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(messageService.getChannelMessages(channelId, currentUser.getId()));
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long channelId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        channelMemberService.removeMember(channelId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable Long channelId,
            @AuthenticationPrincipal User currentUser) {
        channelService.deleteChannel(channelId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
