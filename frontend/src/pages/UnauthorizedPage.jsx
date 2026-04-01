import { useNavigate } from 'react-router-dom';
export default function UnauthorizedPage() {
  const navigate = useNavigate();
  return (
    <div style={{ textAlign: 'center', padding: '4rem' }}>
      <h2>403 - Access Denied</h2>
      <p>You don't have permission to view this page.</p>
      <button onClick={() => navigate('/me')} style={{ padding: '0.5rem 1rem', cursor: 'pointer' }}>
        Go to My Profile
      </button>
    </div>
  );
}
