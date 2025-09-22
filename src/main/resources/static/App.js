import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import MovieCard from './components/MovieCard';
import MovieDetails from './components/MovieDetails';
import StatsDashboard from './components/StatsDashboard';
import Login from './components/Login';
import { Button } from '@mui/material';
import './App.css';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [darkMode, setDarkMode] = useState(false);

  useEffect(() => {
    document.body.classList.toggle('dark-mode', darkMode);
  }, [darkMode]);

  return (
    <Router>
      <div className="app-container">
        <Button
          className="toggle-dark-mode"
          variant="contained"
          onClick={() => setDarkMode(!darkMode)}
        >
          Toggle {darkMode ? 'Light' : 'Dark'} Mode
        </Button>
        <Routes>
          <Route path="/login" element={<Login setToken={setToken} />} />
          <Route path="/" element={<MovieCard token={token} />} />
          <Route path="/movie/:id" element={<MovieDetails token={token} />} />
          <Route path="/stats" element={token ? <StatsDashboard token={token} /> : <Navigate to="/login" />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;