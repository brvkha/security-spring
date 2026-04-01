import { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const { logout } = useAuth();
  const navigate = useNavigate();

  const fetchUsers = () => {
    setLoading(true);
    api.get('/api/admin/users')
      .then((res) => setUsers(res.data))
      .catch(() => setMessage('Failed to load users'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, []);

  const lockUser = async (id) => {
    try {
      await api.patch(`/api/admin/users/${id}/lock`);
      setMessage('User locked');
      fetchUsers();
    } catch (e) {
      setMessage('Error: ' + (e.response?.data?.message || e.message));
    }
  };

  const unlockUser = async (id) => {
    try {
      await api.patch(`/api/admin/users/${id}/unlock`);
      setMessage('User unlocked');
      fetchUsers();
    } catch (e) {
      setMessage('Error: ' + (e.response?.data?.message || e.message));
    }
  };

  const revokeSessions = async (id) => {
    try {
      await api.post(`/api/admin/users/${id}/sessions/revoke`);
      setMessage('Sessions revoked');
    } catch (e) {
      setMessage('Error: ' + (e.response?.data?.message || e.message));
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>Admin Dashboard — Users</h2>
        <div>
          <button onClick={() => navigate('/admin/audit-logs')} style={styles.navBtn}>Audit Logs</button>
          <button onClick={() => navigate('/me')} style={styles.navBtn}>My Profile</button>
          <button onClick={handleLogout} style={{ ...styles.navBtn, background: '#c00', color: 'white' }}>Logout</button>
        </div>
      </div>
      {message && <div style={styles.message}>{message}</div>}
      {loading ? <p>Loading...</p> : (
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>ID</th>
              <th style={styles.th}>Username</th>
              <th style={styles.th}>Role</th>
              <th style={styles.th}>Status</th>
              <th style={styles.th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td style={styles.td}>{u.id}</td>
                <td style={styles.td}>{u.username}</td>
                <td style={styles.td}><span style={styles.badge}>{u.role}</span></td>
                <td style={styles.td}>{u.locked ? '🔒 Locked' : '✅ Active'}</td>
                <td style={styles.td}>
                  {u.locked
                    ? <button onClick={() => unlockUser(u.id)} style={styles.actionBtn}>Unlock</button>
                    : <button onClick={() => lockUser(u.id)} style={{ ...styles.actionBtn, background: '#c00' }}>Lock</button>
                  }
                  <button onClick={() => revokeSessions(u.id)} style={{ ...styles.actionBtn, background: '#e65c00' }}>
                    Revoke Sessions
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const styles = {
  container: { padding: '2rem', fontFamily: 'sans-serif' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' },
  message: { background: '#e8f5e9', padding: '0.75rem', borderRadius: '4px', marginBottom: '1rem' },
  table: { width: '100%', borderCollapse: 'collapse', background: 'white', boxShadow: '0 1px 4px rgba(0,0,0,0.1)' },
  th: { textAlign: 'left', padding: '0.75rem 1rem', borderBottom: '1px solid #eee', background: '#f5f5f5' },
  td: { textAlign: 'left', padding: '0.75rem 1rem', borderBottom: '1px solid #eee' },
  badge: { background: '#1a1a2e', color: 'white', padding: '2px 8px', borderRadius: '12px', fontSize: '0.8rem' },
  actionBtn: { margin: '0 4px', padding: '4px 10px', background: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '0.85rem' },
  navBtn: { margin: '0 4px', padding: '6px 12px', background: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
};
