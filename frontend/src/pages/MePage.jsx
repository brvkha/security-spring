import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axios';

export default function MePage() {
  const { username, role, logout } = useAuth();
  const [meData, setMeData] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/api/me').then((res) => setMeData(res.data)).catch(() => {});
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2>My Profile</h2>
        <table style={styles.table}>
          <tbody>
            <tr><td><strong>Username</strong></td><td>{meData?.username || username}</td></tr>
            <tr><td><strong>Role</strong></td><td><span style={styles.badge}>{meData?.role || role}</span></td></tr>
            <tr><td><strong>Status</strong></td><td>{meData?.locked ? '🔒 Locked' : '✅ Active'}</td></tr>
          </tbody>
        </table>
        {role === 'ADMIN' && (
          <button onClick={() => navigate('/admin/users')} style={{ ...styles.button, marginBottom: '0.5rem' }}>
            Go to Admin Dashboard
          </button>
        )}
        <button onClick={handleLogout} style={{ ...styles.button, background: '#c00' }}>
          Logout
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f0f2f5' },
  card: { background: 'white', padding: '2rem', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', width: '420px' },
  table: { width: '100%', borderCollapse: 'collapse', marginBottom: '1.5rem' },
  badge: { background: '#1a1a2e', color: 'white', padding: '2px 8px', borderRadius: '12px', fontSize: '0.85rem' },
  button: { display: 'block', width: '100%', padding: '0.75rem', background: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', fontSize: '1rem', cursor: 'pointer', marginTop: '0.5rem' },
};
