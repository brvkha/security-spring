import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { RequireAuth } from '../components/RequireAuth';
import { AuthContext } from '../contexts/AuthContext';

const renderWithAuth = (authValue, path = '/protected') => {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <AuthContext.Provider value={authValue}>
        <Routes>
          <Route path="/login" element={<div>Login Page</div>} />
          <Route path="/unauthorized" element={<div>Unauthorized Page</div>} />
          <Route path="/protected" element={
            <RequireAuth>
              <div>Protected Content</div>
            </RequireAuth>
          } />
          <Route path="/admin" element={
            <RequireAuth requiredRole="ADMIN">
              <div>Admin Content</div>
            </RequireAuth>
          } />
        </Routes>
      </AuthContext.Provider>
    </MemoryRouter>
  );
};

describe('RequireAuth', () => {
  it('redirects to login when not authenticated', () => {
    renderWithAuth({ isAuthenticated: false, role: null, username: null });
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  it('renders children when authenticated', () => {
    renderWithAuth({ isAuthenticated: true, role: 'USER', username: 'testuser' });
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });

  it('redirects to unauthorized when role is insufficient', () => {
    render(
      <MemoryRouter initialEntries={['/admin']}>
        <AuthContext.Provider value={{ isAuthenticated: true, role: 'USER', username: 'testuser' }}>
          <Routes>
            <Route path="/login" element={<div>Login Page</div>} />
            <Route path="/unauthorized" element={<div>Unauthorized Page</div>} />
            <Route path="/admin" element={
              <RequireAuth requiredRole="ADMIN">
                <div>Admin Content</div>
              </RequireAuth>
            } />
          </Routes>
        </AuthContext.Provider>
      </MemoryRouter>
    );
    expect(screen.getByText('Unauthorized Page')).toBeInTheDocument();
  });

  it('renders admin content when role matches', () => {
    render(
      <MemoryRouter initialEntries={['/admin']}>
        <AuthContext.Provider value={{ isAuthenticated: true, role: 'ADMIN', username: 'admin' }}>
          <Routes>
            <Route path="/unauthorized" element={<div>Unauthorized Page</div>} />
            <Route path="/admin" element={
              <RequireAuth requiredRole="ADMIN">
                <div>Admin Content</div>
              </RequireAuth>
            } />
          </Routes>
        </AuthContext.Provider>
      </MemoryRouter>
    );
    expect(screen.getByText('Admin Content')).toBeInTheDocument();
  });
});
