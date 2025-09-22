import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { Button, TextField, Select, MenuItem, FormControl, InputLabel, Alert, Grid } from '@mui/material';

function MovieCard({ token }) {
  const [movies, setMovies] = useState([]);
  const [query, setQuery] = useState('');
  const [sort, setSort] = useState('title');
  const [genre, setGenre] = useState('');
  const [page, setPage] = useState(1);
  const [genres, setGenres] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (query) {
      axios.get(`/api/movies/search?query=${query}&sort=${sort}&genre=${genre}&page=${page}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(response => {
          setMovies(response.data);
          setError('');
        })
        .catch(error => setError('Failed to fetch movies: ' + (error.response?.data || error.message)));
    }
    axios.get('/api/movies/stats', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(response => {
        const genreDist = response.data.genreDistribution;
        setGenres(Object.keys(genreDist));
      })
      .catch(error => setError('Failed to fetch stats: ' + (error.response?.data || error.message)));
  }, [query, sort, genre, page, token]);

  const handleDownload = () => {
    window.location.href = `/api/movies/download?query=${query}`;
  };

  return (
    <div className="container">
      <h1>MovieFlix Dashboard</h1>
      {error && <Alert severity="error">{error}</Alert>}
      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            label="Search movies..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} sm={3}>
          <FormControl fullWidth>
            <InputLabel>Sort By</InputLabel>
            <Select value={sort} onChange={e => setSort(e.target.value)}>
              <MenuItem value="title">Title</MenuItem>
              <MenuItem value="rating">Rating</MenuItem>
              <MenuItem value="year">Year</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item xs={12} sm={3}>
          <FormControl fullWidth>
            <InputLabel>Genre</InputLabel>
            <Select value={genre} onChange={e => setGenre(e.target.value)}>
              <MenuItem value="">All Genres</MenuItem>
              {genres.map(g => <MenuItem key={g} value={g}>{g}</MenuItem>)}
            </Select>
          </FormControl>
        </Grid>
      </Grid>
      <Button variant="contained" onClick={handleDownload} style={{ margin: '10px 0' }}>
        Download CSV
      </Button>
      <Grid container spacing={2} className="movie-grid">
        {movies.map(movie => (
          <Grid item xs={12} sm={6} md={4} key={movie.tmdbId}>
            <div className="movie-card">
              <h3>{movie.title}</h3>
              <p>Year: {movie.year}</p>
              <p>Rating: {movie.rating}</p>
              <p>Genres: {movie.genres.join(', ')}</p>
              <Link to={`/movie/${movie.tmdbId}`}>Details</Link>
            </div>
          </Grid>
        ))}
      </Grid>
      <div>
        <Button onClick={() => setPage(page - 1)} disabled={page === 1}>Previous</Button>
        <Button onClick={() => setPage(page + 1)}>Next</Button>
      </div>
    </div>
  );
}

export default MovieCard;