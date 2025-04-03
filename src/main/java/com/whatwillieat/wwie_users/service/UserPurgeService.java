package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPurgeService {

    private final UserRepository userRepository;
    @Transactional
//    @Scheduled(cron = "0 0 3 * * ?") // Runs every day at 3 AM
    @Scheduled(cron = "0 */5 * ? * *")
    public void purgeDeletedUsers() {
        log.info("Purging soft-deleted users...");
        userRepository.deleteByIsDeletedTrue();
        log.info("Purge complete.");
    }
}
