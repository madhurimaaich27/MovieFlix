package com.example.movie.MovieFlix.controller;


import com.example.movie.MovieFlix.model.MovieCache;
import com.example.movie.MovieFlix.repository.MovieCacheRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cache")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MovieCacheRepository cacheRepository;

    public AdminController(MovieCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @GetMapping
    public ResponseEntity<List<MovieCache>> getAllCache() {
        return ResponseEntity.ok(cacheRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCache(@PathVariable String id) {
        try {
            cacheRepository.deleteById(id);
            return ResponseEntity.ok("Cache entry deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete cache entry: " + e.getMessage());
        }
    }
}
