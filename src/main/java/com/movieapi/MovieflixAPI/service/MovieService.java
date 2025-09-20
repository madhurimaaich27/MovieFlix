package com.movieapi.MovieflixAPI.service;


import com.movieapi.MovieflixAPI.model.Movie;
import com.movieapi.MovieflixAPI.dto.OmdbSearchResponse;
import com.movieapi.MovieflixAPI.dto.OmdbMovie;
import com.movieapi.MovieflixAPI.dto.OmdbMovieDetails;
import com.movieapi.MovieflixAPI.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    @Value("${omdb.api.key}")
    private String apiKey;

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    public List<Movie> fetchAndCacheMovies(String search) {
        String url = String.format("http://www.omdbapi.com/?apikey=%s&s=%s", apiKey, search);
        OmdbSearchResponse response = restTemplate.getForObject(url, OmdbSearchResponse.class);
        List<OmdbMovie> movies = response != null ? response.getSearch() : List.of();

        for (OmdbMovie omdbMovie : movies) {
            Movie existing = movieRepository.findByImdbID(omdbMovie.getImdbID());
            boolean isStale = existing != null && existing.getFetchedAt().isBefore(LocalDateTime.now().minusHours(24));

            if (existing == null || isStale) {
                String detailsUrl = String.format("http://www.omdbapi.com/?apikey=%s&i=%s", apiKey, omdbMovie.getImdbID());
                OmdbMovieDetails details = restTemplate.getForObject(detailsUrl, OmdbMovieDetails.class);
                if (details != null) {
                    Movie movie = new Movie();
                    movie.setImdbID(details.getImdbID());
                    movie.setTitle(details.getTitle());
                    movie.setYear(Integer.parseInt(details.getYear()));
                    movie.setGenre(Arrays.asList(details.getGenre().split(", ")));
                    movie.setDirector(details.getDirector());
                    movie.setActors(Arrays.asList(details.getActors().split(", ")));
                    movie.setRating(details.getImdbRating() != null && !details.getImdbRating().equals("N/A") ?
                            Double.parseDouble(details.getImdbRating()) : 0.0);
                    movie.setRuntime(details.getRuntime() != null && !details.getRuntime().equals("N/A") ?
                            Integer.parseInt(details.getRuntime().replace(" min", "")) : 0);
                    movie.setPlot(details.getPlot());
                    movie.setFetchedAt(LocalDateTime.now());
                    movieRepository.save(movie);
                }
            }
        }
        return movieRepository.findByTitleContainingIgnoreCase(search);
    }

    public Movie getMovieById(String id) {
        Movie movie = movieRepository.findByImdbID(id);
        if (movie == null || movie.getFetchedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            String url = String.format("http://www.omdbapi.com/?apikey=%s&i=%s", apiKey, id);
            OmdbMovieDetails details = restTemplate.getForObject(url, OmdbMovieDetails.class);
            if (details != null) {
                movie = new Movie();
                movie.setImdbID(details.getImdbID());
                movie.setTitle(details.getTitle());
                movie.setYear(Integer.parseInt(details.getYear()));
                movie.setGenre(Arrays.asList(details.getGenre().split(", ")));
                movie.setDirector(details.getDirector());
                movie.setActors(Arrays.asList(details.getActors().split(", ")));
                movie.setRating(details.getImdbRating() != null && !details.getImdbRating().equals("N/A") ?
                        Double.parseDouble(details.getImdbRating()) : 0.0);
                movie.setRuntime(details.getRuntime() != null && !details.getRuntime().equals("N/A") ?
                        Integer.parseInt(details.getRuntime().replace(" min", "")) : 0);
                movie.setPlot(details.getPlot());
                movie.setFetchedAt(LocalDateTime.now());
                movieRepository.save(movie);
            }
        }
        return movie;
    }
}