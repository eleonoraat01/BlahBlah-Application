package com.example.blahblahapp.repositories;

import com.example.blahblahapp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChannelIdAndIsActiveTrue(Long channelId);
    List<Message> findByChatIdAndIsActiveTrue(Long chatId);
    List<Message> findBySenderIdAndIsActiveTrue(Long senderId);
}
