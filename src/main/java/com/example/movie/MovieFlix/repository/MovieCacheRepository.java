package com.example.movie.MovieFlix.repository;


import com.example.movie.MovieFlix.model.MovieCache;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MovieCacheRepository extends MongoRepository<MovieCache, String> {
    Optional<MovieCache> findByTmdbIdAndExpiresAtAfter(String tmdbId, LocalDateTime now);
    void deleteByExpiresAtBefore(LocalDateTime now);
}
