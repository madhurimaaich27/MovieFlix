import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Pie, Bar, Line } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, LineElement, PointElement } from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, LineElement, PointElement);

const Stats = () => {
  const [data, setData] = useState({ genreCounts: [], avgRating: 0, avgRuntimeByYear: [], movies: [] });

  useEffect(() => {
    axios.get('http://localhost:8080/api/aggregates')
      .then(res => setData(res.data))
      .catch(err => alert('Failed to fetch stats'));
  }, []);

  const pieData = {
    labels: data.genreCounts.map(g => g._id),
    datasets: [{
      data: data.genreCounts.map(g => g.count),
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
    }]
  };

  const barData = {
    labels: data.genreCounts.map(g => g._id),
    datasets: [{
      label: 'Average Rating by Genre',
      data: data.genreCounts.map(g => {
        const movies = data.movies.filter(m => m.genre.includes(g._id));
        return movies.length ? movies.reduce((sum, m) => sum + m.rating, 0) / movies.length : 0;
      }),
      backgroundColor: '#36A2EB'
    }]
  };

  const lineData = {
    labels: data.avgRuntimeByYear.map(y => y._id),
    datasets: [{
      label: 'Avg Runtime by Year',
      data: data.avgRuntimeByYear.map(y => y.avgRuntime),
      borderColor: '#FF6384',
      fill: false
    }]
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Stats Dashboard</h2>
      <div style={{ marginBottom: '20px' }}>
        <h3>Genre Distribution</h3>
        <Pie data={pieData} />
      </div>
      <div style={{ marginBottom: '20px' }}>
        <h3>Average Ratings by Genre</h3>
        <Bar data={barData} />
      </div>
      <div>
        <h3>Average Runtime by Year</h3>
        <Line data={lineData} />
      </div>
    </div>
  );
};

export default Stats;