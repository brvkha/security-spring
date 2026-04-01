import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { RequireAuth } from './components/RequireAuth';
import LoginPage from './pages/LoginPage';
import MePage from './pages/MePage';
import UsersPage from './pages/admin/UsersPage';
import AuditLogsPage from './pages/admin/AuditLogsPage';
import UnauthorizedPage from './pages/UnauthorizedPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />
          <Route path="/me" element={
            <RequireAuth>
              <MePage />
            </RequireAuth>
          } />
          <Route path="/admin/users" element={
            <RequireAuth requiredRole="ADMIN">
              <UsersPage />
            </RequireAuth>
          } />
          <Route path="/admin/audit-logs" element={
            <RequireAuth requiredRole="ADMIN">
              <AuditLogsPage />
            </RequireAuth>
          } />
          <Route path="/" element={<Navigate to="/me" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
