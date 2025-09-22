import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Pie, Bar, Line } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, BarElement, LineElement, CategoryScale, LinearScale, Title, Tooltip, Legend } from 'chart.js';
import { Alert, Typography, Box } from '@mui/material';

ChartJS.register(ArcElement, BarElement, LineElement, CategoryScale, LinearScale, Title, Tooltip, Legend);

function StatsDashboard({ token }) {
  const [stats, setStats] = useState({});
  const [error, setError] = useState('');

  useEffect(() => {
    axios.get('/api/movies/stats', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(response => {
        setStats(response.data);
        setError('');
      })
      .catch(error => setError('Failed to fetch stats: ' + (error.response?.data || error.message)));
  }, [token]);

  const genreData = {
    labels: Object.keys(stats.genreDistribution || {}),
    datasets: [{
      data: Object.values(stats.genreDistribution || {}),
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'],
    }],
  };

  const ratingData = {
    labels: Object.keys(stats.averageRatingByGenre || {}),
    datasets: [{
      label: 'Average Rating by Genre',
      data: Object.values(stats.averageRatingByGenre || {}),
      backgroundColor: '#36A2EB',
    }],
  };

  const runtimeData = {
    labels: Object.keys(stats.averageRuntimeByYear || {}),
    datasets: [{
      label: 'Average Runtime (minutes)',
      data: Object.values(stats.averageRuntimeByYear || {}),
      borderColor: '#FF6384',
      fill: false,
    }],
  };

  return (
    <div className="container">
      <Typography variant="h4">Stats Dashboard</Typography>
      {error && <Alert severity="error">{error}</Alert>}
      <Box className="chart-container">
        <Typography variant="h6">Genre Distribution</Typography>
        <Pie data={genreData} />
      </Box>
      <Box className="chart-container">
        <Typography variant="h6">Average Ratings by Genre</Typography>
        <Bar data={ratingData} />
      </Box>
      <Box className="chart-container">
        <Typography variant="h6">Average Runtime by Year</Typography>
        <Line data={runtimeData} />
      </Box>
    </div>
  );
}

export default StatsDashboard;