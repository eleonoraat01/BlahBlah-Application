package com.example.blahblahapp.repositories;

import com.example.blahblahapp.entities.UserSocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSocialLinkRepository extends JpaRepository<UserSocialLink, Long> {
    List<UserSocialLink> findByUserId(Long userId);
}
