package com.example.blahblahapp.repositories;

import com.example.blahblahapp.entities.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUser_IdAndFriend_Id(Long userId, Long friendId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " +
            "WHERE (f.user.id = :userId AND f.friend.id = :friendId) OR " +
            "(f.user.id = :friendId AND f.friend.id = :userId)")
    boolean existsByUserIds(Long userId, Long friendId);

    @Query("SELECT f FROM Friendship f WHERE " +
            "f.user.id = :userId OR f.friend.id = :userId")
    List<Friendship> findFriendsByUserId(Long userId);

    @Query("SELECT f FROM Friendship f " +
            "WHERE (f.user.id = :userId AND f.friend.id = :friendId) OR " +
            "(f.user.id = :friendId AND f.friend.id = :userId)")
    Optional<Friendship> findByUserIds(Long userId, Long friendId);
}
