import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { Typography, Box } from '@mui/material';

const MovieDetails = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/movies/${id}`)
      .then(res => setMovie(res.data))
      .catch(err => alert('Failed to fetch movie details'));
  }, [id]);

  if (!movie) return <Typography>Loading...</Typography>;

  return (
    <Box style={{ padding: '20px' }}>
      <Typography variant="h4">{movie.title}</Typography>
      <Typography variant="body1"><strong>Plot:</strong> {movie.plot}</Typography>
      <Typography variant="body1"><strong>Cast:</strong> {movie.actors.join(', ')}</Typography>
      <Typography variant="body1"><strong>Runtime:</strong> {movie.runtime} min</Typography>
      <Typography variant="body1"><strong>Genre:</strong> {movie.genre.join(', ')}</Typography>
      <Typography variant="body1"><strong>Director:</strong> {movie.director}</Typography>
      <Typography variant="body1"><strong>Rating:</strong> {movie.rating}</Typography>
      <a href={`https://www.imdb.com/title/${movie.imdbID}`} target="_blank" rel="noopener noreferrer">IMDb Page</a>
    </Box>
  );
};

export default MovieDetails;