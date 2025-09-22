package com.example.movie.MovieFlix.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.movie.MovieFlix.dto.MovieDTO;
import com.example.movie.MovieFlix.model.MovieCache;
import com.example.movie.MovieFlix.repository.MovieCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieCacheRepository cacheRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";

    public MovieService(MovieCacheRepository cacheRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.cacheRepository = cacheRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<MovieDTO> searchMovies(String query, String sort, String genre, int page, int size) throws Exception {
        String cacheKey = "search:" + query + ":" + sort + ":" + genre + ":" + page;
        Optional<MovieCache> cached = cacheRepository.findByTmdbIdAndExpiresAtAfter(cacheKey, LocalDateTime.now());
        if (cached.isPresent()) {
            return Collections.singletonList(cached.get().getData());
        }

        String url = TMDB_BASE_URL + "/search/movie?api_key=" + tmdbApiKey + "&query=" + query + "&page=" + page;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = response != null ? (List<Map<String, Object>>) response.get("results") : Collections.emptyList();

        List<MovieDTO> movies = results.stream()
                .map(this::mapToMovieDTO)
                .filter(movie -> genre == null || movie.getGenres().contains(genre))
                .sorted((a, b) -> sortByField(a, b, sort))
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        if (!movies.isEmpty()) {
            cacheRepository.save(new MovieCache(cacheKey, query, movies.get(0)));
        }
        return movies;
    }

    public MovieDTO getMovieDetails(String id) throws Exception {
        String cacheKey = "id:" + id;
        Optional<MovieCache> cached = cacheRepository.findByTmdbIdAndExpiresAtAfter(cacheKey, LocalDateTime.now());
        if (cached.isPresent()) {
            return cached.get().getData();
        }

        String url = TMDB_BASE_URL + "/movie/" + id + "?api_key=" + tmdbApiKey + "&append_to_response=credits";
        Map<String, Object> movieData = restTemplate.getForObject(url, Map.class);
        MovieDTO movieDTO = mapToMovieDTO(movieData);

        cacheRepository.save(new MovieCache(cacheKey, movieDTO.getTitle(), movieDTO));
        return movieDTO;
    }

    public Map<String, Object> getStats() {
        List<MovieCache> cachedMovies = cacheRepository.findAll();
        Map<String, Long> genreDistribution = cachedMovies.stream()
                .flatMap(cache -> cache.getData().getGenres().stream())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));

        Map<String, Double> avgRatingByGenre = cachedMovies.stream()
                .flatMap(cache -> cache.getData().getGenres().stream()
                        .map(genre -> new AbstractMap.SimpleEntry<>(genre, cache.getData().getRating())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)
                ));

        Double averageRating = cachedMovies.stream()
                .mapToDouble(cache -> cache.getData().getRating())
                .average()
                .orElse(0.0);

        Map<Integer, Double> avgRuntimeByYear = cachedMovies.stream()
                .collect(Collectors.groupingBy(
                        cache -> cache.getData().getYear(),
                        Collectors.averagingInt(cache -> cache.getData().getRuntime())
                ));

        return Map.of(
                "genreDistribution", genreDistribution,
                "averageRating", averageRating,
                "averageRatingByGenre", avgRatingByGenre,
                "averageRuntimeByYear", avgRuntimeByYear
        );
    }

    private MovieDTO mapToMovieDTO(Map<String, Object> data) {
        if (data == null) {
            return new MovieDTO();
        }
        String releaseDate = (String) data.get("release_date");
        int year = releaseDate != null && !releaseDate.isEmpty() ? Integer.parseInt(releaseDate.split("-")[0]) : 0;
        List<String> genres = data.get("genres") != null
                ? ((List<Map<String, Object>>) data.get("genres"))
                .stream()
                .map(g -> (String) g.get("name"))
                .collect(Collectors.toList())
                : Collections.emptyList();
        List<String> actors = data.containsKey("credits") && data.get("credits") != null
                ? ((List<Map<String, Object>>) ((Map) data.get("credits")).get("cast"))
                .stream()
                .limit(3)
                .map(c -> (String) c.get("name"))
                .collect(Collectors.toList())
                : Collections.emptyList();
        String director = data.containsKey("credits") && data.get("credits") != null
                ? ((List<Map<String, Object>>) ((Map) data.get("credits")).get("crew"))
                .stream()
                .filter(c -> "Director".equals(c.get("job")))
                .map(c -> (String) c.get("name"))
                .findFirst()
                .orElse("")
                : "";

        return new MovieDTO(
                String.valueOf(data.get("id")),
                (String) data.get("title"),
                year,
                genres,
                director,
                actors,
                data.get("vote_average") != null ? ((Number) data.get("vote_average")).doubleValue() : 0.0,
                data.get("runtime") != null ? (Integer) data.get("runtime") : 0,
                (String) data.get("overview")
        );
    }

    private int sortByField(MovieDTO a, MovieDTO b, String sort) {
        if ("rating".equalsIgnoreCase(sort)) {
            return Double.compare(b.getRating(), a.getRating());
        } else if ("year".equalsIgnoreCase(sort)) {
            return Integer.compare(b.getYear(), a.getYear());
        }
        return a.getTitle().compareTo(b.getTitle());
    }
}
