package com.example.chat_application.repository;



import com.example.chat_application.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
    SELECT u
    FROM User u
    LEFT JOIN UserSettings s ON s.user = u
    WHERE
        (s.connectionPolicy IS NULL OR s.connectionPolicy = 'EVERYONE')
        AND (
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
""")
    List<User> searchPublicUsers(@Param("keyword") String keyword);


    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
            SET u.displayName = :displayName,
                u.phoneNumber = :phoneNumber,
                u.profileImageUrl = :profileImageUrl,
                u.bio = :bio,
                u.profileCompleted = true
            WHERE u.username = :username
            """)
    default void updateProfileOnly(
            @Param("username") String username,
            @Param("displayName") String displayName,
            @Param("phoneNumber") String phoneNumber,
            @Param("profileImageUrl") String profileImageUrl,
            @Param("bio") String bio
    ) {

    }
    // UserRepository.java
    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findUserIdByUsername(@Param("username") String username);

}
