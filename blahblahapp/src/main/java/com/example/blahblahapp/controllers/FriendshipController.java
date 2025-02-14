package com.example.blahblahapp.controllers;

import com.example.blahblahapp.dtos.user.AddFriendDTO;
import com.example.blahblahapp.dtos.user.FriendshipDTO;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.services.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendshipDTO>> getUserFriends(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(friendshipService.getUserFriends(currentUser.getId()));
    }

    @PostMapping()
    public ResponseEntity<FriendshipDTO> addFriend(
            @Valid @RequestBody AddFriendDTO request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendshipService.addFriend(currentUser.getId(), request.getFriendId()));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable Long friendId,
            @AuthenticationPrincipal User currentUser) {
        friendshipService.removeFriend(friendId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
