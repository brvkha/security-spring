import { createContext, useContext, useState, useCallback } from 'react';
import { authStore } from '../store/authStore';
import api from '../api/axios';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(authStore.isAuthenticated());
  const [role, setRole] = useState(authStore.getRole());
  const [username, setUsername] = useState(authStore.getUsername());

  const login = useCallback(async (username, password) => {
    const res = await api.post('/api/auth/login', { username, password });
    const { accessToken, role } = res.data;
    authStore.setAuth(accessToken, role, username);
    setIsAuthenticated(true);
    setRole(role);
    setUsername(username);
    return role;
  }, []);

  const logout = useCallback(async () => {
    try {
      await api.post('/api/auth/logout');
    } catch (_) {}
    authStore.clear();
    setIsAuthenticated(false);
    setRole(null);
    setUsername(null);
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, role, username, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
