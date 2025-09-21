import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import jwtDecode from 'jwt-decode';
import Search from './components/Search';
import MovieDetails from './components/MovieDetails';
import Stats from './components/Stats';
import Login from './components/Login';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) return <Navigate to="/login" />;
  try {
    jwtDecode(token);
    return children;
  } catch (err) {
    return <Navigate to="/login" />;
  }
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Search />} />
        <Route path="/movie/:id" element={<MovieDetails />} />
        <Route path="/stats" element={<ProtectedRoute><Stats /></ProtectedRoute>} />
      </Routes>
    </Router>
  );
}

export default App;
