package com.example.blahblahapp.services;

import com.example.blahblahapp.dtos.user.FriendshipDTO;
import com.example.blahblahapp.dtos.user.UserDTO;
import com.example.blahblahapp.entities.Friendship;
import com.example.blahblahapp.entities.Message;
import com.example.blahblahapp.entities.User;
import com.example.blahblahapp.exceptions.AccessDeniedException;
import com.example.blahblahapp.exceptions.ResourceNotFoundException;
import com.example.blahblahapp.repositories.FriendshipRepository;
import com.example.blahblahapp.repositories.UserRepository;
import com.example.blahblahapp.utils.DTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipDTO addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new AccessDeniedException("Cannot add yourself as friend");
        }

        if (friendshipRepository.existsByUserIds(userId, friendId)) {
            throw new AccessDeniedException("You and user with ID " + friendId + " are already friends!");
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User friend = userRepository
                .findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));

        Friendship friendship = Friendship
                .builder()
                .user(user)
                .friend(friend)
                .build();

        log.info("Users with ID's {} and {} are now friends", userId, friendId);

        return DTOMapper.mapFriendshipToDTO(friendshipRepository.save(friendship), userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository
                .findByUserIds(userId, friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        log.info("Users with ID's {} and {} are no longer friends", userId, friendId);

        friendshipRepository.delete(friendship);
    }

    public List<FriendshipDTO> getUserFriends(Long userId) {

        return friendshipRepository
                .findFriendsByUserId(userId)
                .stream()
                .map(friendship -> DTOMapper.mapFriendshipToDTO(friendship, userId))
//                .map(DTOMapper::mapFriendshipToDTO)
                .collect(Collectors.toList());
    }
}
