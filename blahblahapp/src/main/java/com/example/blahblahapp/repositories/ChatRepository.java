package com.example.blahblahapp.repositories;

import com.example.blahblahapp.entities.Chat;
import com.example.blahblahapp.entities.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
            Long user1Id, Long user2Id,
            Long altUser1Id, Long altUser2Id);

    @Query("SELECT c FROM Chat c WHERE " +
            "c.user1.id = :user1Id OR c.user2.id = :user1Id")
    List<Chat> findByUser1Id(Long user1Id);
}
