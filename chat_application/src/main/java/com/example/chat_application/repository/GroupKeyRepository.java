package com.example.chat_application.repository;

import com.example.chat_application.model.GroupKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupKeyRepository extends JpaRepository<GroupKey, Long> {

    List<GroupKey> findByChatRoomId(Long chatRoomId);

    Optional<GroupKey> findByChatRoomIdAndUserId(
            Long chatRoomId,
            Long userId
    );
}
