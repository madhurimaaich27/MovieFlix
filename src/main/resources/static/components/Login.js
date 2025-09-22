import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { TextField, Button, Alert, Box, Typography } from '@mui/material';

function Login({ setToken }) {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const response = await axios.post('/api/auth/login', credentials);
      const { token } = response.data;
      localStorage.setItem('token', token);
      setToken(token);
      navigate('/');
    } catch (error) {
      setError('Login failed: ' + (error.response?.data || error.message));
    }
  };

  return (
    <div className="container">
      <Typography variant="h4">Login</Typography>
      {error && <Alert severity="error">{error}</Alert>}
      <Box>
        <TextField
          label="Username"
          value={credentials.username}
          onChange={e => setCredentials({ ...credentials, username: e.target.value })}
          fullWidth
          margin="normal"
        />
        <TextField
          label="Password"
          type="password"
          value={credentials.password}
          onChange={e => setCredentials({ ...credentials, password: e.target.value })}
          fullWidth
          margin="normal"
        />
        <Button variant="contained" onClick={handleLogin}>Login</Button>
      </Box>
    </div>
  );
}

export default Login;