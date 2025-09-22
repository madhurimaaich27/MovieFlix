package com.example.movie.MovieFlix.dto;


import java.util.List;

public class MovieDTO {
    private String tmdbId;
    private String title;
    private int year;
    private List<String> genres;
    private String director;
    private List<String> actors;
    private double rating;
    private int runtime;
    private String plot;

    public MovieDTO() {}

    public MovieDTO(String tmdbId, String title, int year, List<String> genres, String director, List<String> actors, double rating, int runtime, String plot) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.director = director;
        this.actors = actors;
        this.rating = rating;
        this.runtime = runtime;
        this.plot = plot;
    }

    // Getters and setters
    public String getTmdbId() { return tmdbId; }
    public void setTmdbId(String tmdbId) { this.tmdbId = tmdbId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public List<String> getActors() { return actors; }
    public void setActors(List<String> actors) { this.actors = actors; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }
    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }
}