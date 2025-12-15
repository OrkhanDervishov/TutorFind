import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);
const STORAGE_KEY = "tf_auth";

export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState(null);

  useEffect(() => {
    const stored = window.localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        // Backward compatibility for the old shape `{ name, email, role }`
        if (parsed && !parsed.token && parsed.name) {
          setAuth({
            user: {
              id: parsed.id,
              email: parsed.email,
              firstName: parsed.name,
              lastName: "",
              role: (parsed.role || "").toUpperCase()
            },
            token: null
          });
        } else {
          setAuth(parsed);
        }
      } catch {
        window.localStorage.removeItem(STORAGE_KEY);
      }
    }
  }, []);

  const persist = (payload) => {
    setAuth(payload);
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
  };

  const login = (payload) => {
    persist(payload);
  };

  const logout = () => {
    setAuth(null);
    window.localStorage.removeItem(STORAGE_KEY);
  };

  return (
    <AuthContext.Provider
      value={{
        user: auth?.user,
        token: auth?.token,
        login,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
