import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../api/axios';

export default function AuditLogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { logout } = useAuth();
  const navigate = useNavigate();

  const fetchLogs = (p = 0) => {
    setLoading(true);
    api.get(`/api/admin/audit-logs?page=${p}&size=20`)
      .then((res) => {
        setLogs(res.data.content);
        setTotalPages(res.data.totalPages);
        setPage(p);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchLogs(0); }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>Admin Dashboard — Audit Logs</h2>
        <div>
          <button onClick={() => navigate('/admin/users')} style={styles.navBtn}>Users</button>
          <button onClick={() => navigate('/me')} style={styles.navBtn}>My Profile</button>
          <button onClick={handleLogout} style={{ ...styles.navBtn, background: '#c00', color: 'white' }}>Logout</button>
        </div>
      </div>
      {loading ? <p>Loading...</p> : (
        <>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Time</th>
                <th style={styles.th}>Event</th>
                <th style={styles.th}>Actor</th>
                <th style={styles.th}>Target</th>
                <th style={styles.th}>Result</th>
                <th style={styles.th}>Details</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td style={styles.td}>{new Date(log.timestamp).toLocaleString()}</td>
                  <td style={styles.td}><span style={styles.badge}>{log.eventType}</span></td>
                  <td style={styles.td}>{log.actor}</td>
                  <td style={styles.td}>{log.target || '-'}</td>
                  <td style={{ ...styles.td, color: log.result === 'SUCCESS' ? 'green' : 'red' }}>{log.result}</td>
                  <td style={styles.td}>{log.details || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={styles.pagination}>
            <button disabled={page === 0} onClick={() => fetchLogs(page - 1)} style={styles.pageBtn}>Previous</button>
            <span style={{ margin: '0 1rem' }}>Page {page + 1} of {totalPages}</span>
            <button disabled={page >= totalPages - 1} onClick={() => fetchLogs(page + 1)} style={styles.pageBtn}>Next</button>
          </div>
        </>
      )}
    </div>
  );
}

const styles = {
  container: { padding: '2rem', fontFamily: 'sans-serif' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' },
  table: { width: '100%', borderCollapse: 'collapse', background: 'white', boxShadow: '0 1px 4px rgba(0,0,0,0.1)' },
  th: { textAlign: 'left', padding: '0.75rem 1rem', borderBottom: '1px solid #eee', background: '#f5f5f5' },
  td: { textAlign: 'left', padding: '0.75rem 1rem', borderBottom: '1px solid #eee' },
  badge: { background: '#1a1a2e', color: 'white', padding: '2px 8px', borderRadius: '12px', fontSize: '0.8rem' },
  pagination: { display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: '1rem' },
  pageBtn: { padding: '6px 14px', background: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
  navBtn: { margin: '0 4px', padding: '6px 12px', background: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
};
