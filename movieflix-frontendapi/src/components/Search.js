import React, { useState } from 'react';
import axios from 'axios';
import { Grid, TextField, Button, Select, MenuItem, FormControl, InputLabel, Card, CardContent, Typography } from '@mui/material';
import { Link } from 'react-router-dom';

const Search = () => {
  const [search, setSearch] = useState('');
  const [movies, setMovies] = useState([]);
  const [sort, setSort] = useState('rating:desc');
  const [genreFilter, setGenreFilter] = useState('');

  const handleSearch = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/movies?search=${search}&sort=${sort}&filter=genre:${genreFilter}`);
      setMovies(res.data);
    } catch (err) {
      alert('Failed to fetch movies');
    }
  };

  const handleDownload = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/movies/download?search=${search}&sort=${sort}&filter=genre:${genreFilter}`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'movies.csv');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      alert('Failed to download CSV');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <TextField
        value={search}
        onChange={e => setSearch(e.target.value)}
        label="Search Movies"
        variant="outlined"
        style={{ marginRight: '10px' }}
      />
      <FormControl style={{ minWidth: '120px', marginRight: '10px' }}>
        <InputLabel>Sort</InputLabel>
        <Select value={sort} onChange={e => setSort(e.target.value)}>
          <MenuItem value="rating:desc">Rating Desc</MenuItem>
          <MenuItem value="rating:asc">Rating Asc</MenuItem>
          <MenuItem value="year:desc">Year Desc</MenuItem>
          <MenuItem value="year:asc">Year Asc</MenuItem>
        </Select>
      </FormControl>
      <FormControl style={{ minWidth: '120px', marginRight: '10px' }}>
        <InputLabel>Genre</InputLabel>
        <Select value={genreFilter} onChange={e => setGenreFilter(e.target.value)}>
          <MenuItem value="">All</MenuItem>
          <MenuItem value="Action">Action</MenuItem>
          <MenuItem value="Sci-Fi">Sci-Fi</MenuItem>
          <MenuItem value="Drama">Drama</MenuItem>
        </Select>
      </FormControl>
      <Button variant="contained" onClick={handleSearch}>Search</Button>
      <Button variant="outlined" onClick={handleDownload} style={{ marginLeft: '10px' }}>Download CSV</Button>
      <Grid container spacing={2} style={{ marginTop: '20px' }}>
        {movies.map(movie => (
          <Grid item xs={12} sm={6} md={4} key={movie.imdbID}>
            <Card>
              <CardContent>
                <Typography variant="h6">{movie.title}</Typography>
                <Typography>Year: {movie.year}</Typography>
                <Typography>Rating: {movie.rating}</Typography>
                <Typography>Runtime: {movie.runtime} min</Typography>
                <Link to={`/movie/${movie.imdbID}`}>View Details</Link>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default Search;