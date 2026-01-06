package com.example.chat_application.scheduler;

import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class PresenceCleanupJob {

    private final UserRepository userRepository;

    public PresenceCleanupJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Runs every 30 seconds
     * Marks inactive users as OFFLINE
     */
    @Scheduled(fixedRate = 30000)
    public void markInactiveUsersOffline() {

        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(60);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getLastSeen() != null &&
                    user.getLastSeen().isBefore(cutoff) &&
                    user.getStatus() == User.Status.ONLINE) {

                user.setStatus(User.Status.OFFLINE);
                userRepository.save(user);
            }
        }
    }
}
