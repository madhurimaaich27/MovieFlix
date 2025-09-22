package com.example.movie.MovieFlix.scheduler;


import com.example.movie.MovieFlix.repository.MovieCacheRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CacheCleanupScheduler {

    private final MovieCacheRepository cacheRepository;

    public CacheCleanupScheduler(MovieCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanExpiredCache() {
        cacheRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        System.out.println("Expired cache entries cleaned");
    }
}
