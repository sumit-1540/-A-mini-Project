import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    try {
      const savedToken = localStorage.getItem('smartshelf_token');
      const savedUser = localStorage.getItem('smartshelf_user');
      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      }
    } catch (e) {
      console.error('Failed to restore session:', e);
      localStorage.removeItem('smartshelf_token');
      localStorage.removeItem('smartshelf_user');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const login = async (credentials) => {
    const data = await authService.login(credentials);
    const loggedInUser = {
      id: data.id,
      username: data.username,
      fullName: data.fullName,
      email: data.email,
      role: data.role,
      active: true,
    };

    setToken(data.token);
    setUser(loggedInUser);
    localStorage.setItem('smartshelf_token', data.token);
    localStorage.setItem('smartshelf_user', JSON.stringify(loggedInUser));
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('smartshelf_token');
    localStorage.removeItem('smartshelf_user');
  };

  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isCashier = user?.role === 'ROLE_CASHIER' || isAdmin;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        isLoading,
        login,
        logout,
        isAdmin,
        isCashier,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
