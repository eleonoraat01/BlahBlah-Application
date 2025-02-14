package com.example.blahblahapp.controllers;

import com.example.blahblahapp.dtos.chat.ChatCreateDTO;
import com.example.blahblahapp.dtos.chat.ChatDTO;
import com.example.blahblahapp.dtos.message.MessageCreateDTO;
import com.example.blahblahapp.dtos.message.MessageDTO;
import com.example.blahblahapp.dtos.message.MessageUpdateDTO;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.services.ChatService;
import com.example.blahblahapp.services.MessageService;
import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ChatDTO> createChat(
            @Valid @RequestBody ChatCreateDTO request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createChat(request, currentUser.getId()));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> getChat(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.getChat(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody MessageCreateDTO request,
            @AuthenticationPrincipal User currentUser){
        request.setChatId(chatId); // To ensure that chatId is setted
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createChatMessage(request, currentUser.getId()));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(messageService.getChatMessages(chatId, currentUser.getId()));
    }

    @PatchMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            @RequestBody MessageUpdateDTO updateDTO,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(messageService.updateMessage(messageId, updateDTO, currentUser.getId()));
    }
}
