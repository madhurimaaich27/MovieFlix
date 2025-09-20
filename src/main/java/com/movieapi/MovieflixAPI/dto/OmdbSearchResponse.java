package com.movieapi.MovieflixAPI.dto;


import java.util.List;

public class OmdbSearchResponse {
    private List<OmdbMovie> Search;

    public List<OmdbMovie> getSearch() {
        return Search;
    }

    public void setSearch(List<OmdbMovie> search) {
        Search = search;
    }
}
