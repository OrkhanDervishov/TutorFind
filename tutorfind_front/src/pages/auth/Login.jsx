import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import api from "../../api/client";

const Login = () => {
  const [form, setForm] = useState({
    email: "",
    password: ""
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await api.login(form.email, form.password);
      login({
        token: res.token,
        user: { ...res.user, role: res.user.role?.toUpperCase() }
      });
      navigate("/");
    } catch (err) {
      setError(err.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-card page-card--narrow">
      <header className="page-card__header">
        <p className="eyebrow">Welcome back</p>
        <h1>Log in</h1>
      </header>
      <form className="stacked-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Email</span>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            autoComplete="email"
            required
          />
        </label>
        <label className="field">
          <span>Password</span>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="current-password"
            required
          />
        </label>
        <div className="form-actions">
          <button type="submit" className="btn btn--primary" disabled={loading}>
            {loading ? "Signing in..." : "Log In"}
          </button>
        </div>
        {error && <p className="hint text-red-500">{error}</p>}
      </form>
      <p className="hint">
        New to TutorFind? <Link to="/signup">Create an account</Link>.
      </p>
    </div>
  );
};

export default Login;
