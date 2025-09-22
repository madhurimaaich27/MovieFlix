package com.example.movie.MovieFlix.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.movie.MovieFlix.dto.MovieDTO;

import java.time.LocalDateTime;

@Document(collection = "movie_cache")
public class MovieCache {
    @Id
    private String id;
    private String tmdbId;
    private String title;
    private MovieDTO data;
    private LocalDateTime cachedAt;
    private LocalDateTime expiresAt;

    public MovieCache() {}

    public MovieCache(String tmdbId, String title, MovieDTO data) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.data = data;
        this.cachedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTmdbId() { return tmdbId; }
    public void setTmdbId(String tmdbId) { this.tmdbId = tmdbId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public MovieDTO getData() { return data; }
    public void setData(MovieDTO data) { this.data = data; }
    public LocalDateTime getCachedAt() { return cachedAt; }
    public void setCachedAt(LocalDateTime cachedAt) { this.cachedAt = cachedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}