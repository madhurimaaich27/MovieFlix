import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Alert, Typography, Box } from '@mui/material';

function MovieDetails({ token }) {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    axios.get(`/api/movies/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(response => {
        setMovie(response.data);
        setError('');
      })
      .catch(error => setError('Failed to fetch movie details: ' + (error.response?.data || error.message)));
  }, [id, token]);

  if (!movie) return <div>Loading...</div>;

  return (
    <div className="container">
      {error && <Alert severity="error">{error}</Alert>}
      <Typography variant="h4">{movie.title}</Typography>
      <Box>
        <Typography><strong>Year:</strong> {movie.year}</Typography>
        <Typography><strong>Genres:</strong> {movie.genres.join(', ')}</Typography>
        <Typography><strong>Director:</strong> {movie.director}</Typography>
        <Typography><strong>Actors:</strong> {movie.actors.join(', ')}</Typography>
        <Typography><strong>Rating:</strong> {movie.rating}</Typography>
        <Typography><strong>Runtime:</strong> {movie.runtime} minutes</Typography>
        <Typography><strong>Plot:</strong> {movie.plot}</Typography>
        <Typography>
          <a href={`https://www.imdb.com/title/${movie.tmdbId}`} target="_blank" rel="noopener noreferrer">IMDb</a>
        </Typography>
      </Box>
    </div>
  );
}

export default MovieDetails;