package com.example.blahblahapp.repositories;

import com.example.blahblahapp.entities.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    List<ChannelMember> findByChannelIdAndIsActiveTrue(Long channelId);
    List<ChannelMember> findByUserId(Long userId);
    Optional<ChannelMember> findByChannelIdAndUserId(Long channelId, Long userId);
    boolean existsByChannelIdAndUserId(Long channelId, Long userId);
    boolean existsByChannelIdAndUserIdAndIsActiveTrue(Long channelId, Long userId);
}
