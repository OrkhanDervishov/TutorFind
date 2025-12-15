import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../api/client";
import { useAuth } from "../../context/AuthContext";

const Signup = () => {
  const [form, setForm] = useState({
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    age: "",
    password: "",
    confirmPassword: "",
    role: "LEARNER"
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleRole = (role) => {
    setForm((prev) => ({ ...prev, role }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    setLoading(true);
    try {
      await api.register({
        username: form.username,
        email: form.email,
        password: form.password,
        role: form.role,
        firstName: form.firstName,
        lastName: form.lastName,
        age: form.age ? Number(form.age) : null,
        phoneNumber: form.phoneNumber
      });
      const res = await api.login(form.email, form.password);
      login({
        token: res.token,
        user: { ...res.user, role: res.user.role?.toUpperCase() }
      });
      navigate("/");
    } catch (err) {
      setError(err.message || "Signup failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-card page-card--narrow">
      <header className="page-card__header">
        <p className="eyebrow">Join the community</p>
        <h1>Sign up</h1>
      </header>
      <div className="role-toggle">
        <button
          type="button"
          className={`role-card ${form.role === "LEARNER" ? "is-active" : ""}`}
          onClick={() => handleRole("LEARNER")}
        >
          <strong>Learner</strong>
          <span>Find tutors nearby and book easily.</span>
        </button>
        <button
          type="button"
          className={`role-card ${form.role === "TUTOR" ? "is-active" : ""}`}
          onClick={() => handleRole("TUTOR")}
        >
          <strong>Tutor</strong>
          <span>Show your expertise and manage availability.</span>
        </button>
      </div>

      <form className="stacked-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Username</span>
          <input
            name="username"
            value={form.username}
            onChange={handleChange}
            required
          />
        </label>
        <div className="two-col">
          <label className="field">
            <span>First name</span>
            <input
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              autoComplete="given-name"
              required
            />
          </label>
          <label className="field">
            <span>Last name</span>
            <input
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              autoComplete="family-name"
              required
            />
          </label>
        </div>
        <div className="two-col">
          <label className="field">
            <span>Phone</span>
            <input
              name="phoneNumber"
              maxLength="10"
              value={form.phoneNumber}
              onChange={handleChange}
              placeholder="10 digits max"
            />
          </label>
          <label className="field">
            <span>Age</span>
            <input
              name="age"
              type="number"
              min="18"
              max="100"
              value={form.age}
              onChange={handleChange}
            />
          </label>
        </div>
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
            autoComplete="new-password"
            required
          />
        </label>
        <label className="field">
          <span>Confirm password</span>
          <input
            type="password"
            name="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
            autoComplete="new-password"
            required
          />
        </label>

        {form.role === "TUTOR" ? (
          <p className="hint">
            You will complete your profile details (Subjects, Price,
            Availability) after registration.
          </p>
        ) : null}

        <button type="submit" className="btn btn--primary" disabled={loading}>
          {loading ? "Creating account..." : "Create Account"}
        </button>
        {error && <p className="hint text-red-500">{error}</p>}
      </form>
      <p className="hint">
        Already have an account? <Link to="/login">Log in</Link>.
      </p>
    </div>
  );
};

export default Signup;
