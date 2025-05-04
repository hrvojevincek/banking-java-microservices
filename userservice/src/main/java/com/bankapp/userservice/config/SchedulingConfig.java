package com.bankapp.userservice.config;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.bankapp.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final UserService userService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupUnverifiedUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1);
        int deletedCount = userService.cleanupUnverifiedUsers(cutoffTime);

        if (deletedCount > 0) {
            log.info("Cleaned up {} unverified users", deletedCount);
        }
    }
}