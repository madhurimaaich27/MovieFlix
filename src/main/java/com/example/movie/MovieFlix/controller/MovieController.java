package com.example.movie.MovieFlix.controller;


import com.example.movie.MovieFlix.dto.MovieDTO;
import com.example.movie.MovieFlix.service.MovieService;
import com.opencsv.CSVWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Query parameter is required");
            }
            List<MovieDTO> movies = movieService.searchMovies(query, sort, genre, page, size);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch movies: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieDetails(@PathVariable String id) {
        try {
            MovieDTO movie = movieService.getMovieDetails(id);
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch movie details: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Object> stats = movieService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch stats: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public void downloadMovies(@RequestParam String query, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=movies.csv");

        List<MovieDTO> movies = movieService.searchMovies(query, "title", null, 1, Integer.MAX_VALUE);
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            String[] header = {"Title", "Year", "Genres", "Director", "Actors", "Rating", "Runtime"};
            writer.writeNext(header);
            for (MovieDTO movie : movies) {
                writer.writeNext(new String[]{
                        movie.getTitle(),
                        String.valueOf(movie.getYear()),
                        String.join(",", movie.getGenres()),
                        movie.getDirector(),
                        String.join(",", movie.getActors()),
                        String.valueOf(movie.getRating()),
                        String.valueOf(movie.getRuntime())
                });
            }
        }
    }
}
