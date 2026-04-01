// In-memory access token store (NOT localStorage for security)
let _accessToken = null;
let _role = null;
let _username = null;

export const authStore = {
  getAccessToken: () => _accessToken,
  getRole: () => _role,
  getUsername: () => _username,
  setAuth: (token, role, username) => {
    _accessToken = token;
    _role = role;
    _username = username;
  },
  clear: () => {
    _accessToken = null;
    _role = null;
    _username = null;
  },
  isAuthenticated: () => _accessToken !== null,
};
