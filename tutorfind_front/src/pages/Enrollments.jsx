import { useEffect, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const Enrollments = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [status, setStatus] = useState("ACTIVE");
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const load = async (statusFilter = status) => {
    if (!token || role !== "LEARNER") return;
    setLoading(true);
    setError("");
    try {
      const data = await api.myEnrollments(
        token,
        statusFilter === "ALL" ? undefined : statusFilter
      );
      setItems(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Unable to load enrollments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [token, role]);

  const handleDrop = async (enrollmentId) => {
    setMessage("");
    setError("");
    try {
      await api.dropEnrollment(token, enrollmentId);
      setMessage("Dropped from class.");
      load();
    } catch (err) {
      setError(err.message || "Drop failed");
    }
  };

  if (role !== "LEARNER") {
    return <p className="hint">Learner login required to view enrollments.</p>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Enrollments</p>
          <h1>My Classes</h1>
          {message && <p className="hint">{message}</p>}
          {error && <p className="hint text-red-500">{error}</p>}
        </div>
        <label className="field field--inline">
          <span>Status</span>
          <select
            value={status}
            onChange={(e) => {
              const value = e.target.value;
              setStatus(value);
              load(value);
            }}
          >
            <option value="ACTIVE">Active</option>
            <option value="ALL">All</option>
            <option value="COMPLETED">Completed</option>
            <option value="DROPPED">Dropped</option>
          </select>
        </label>
      </div>

      {loading && <p className="hint">Loading...</p>}
      <div className="requests-list">
        {!loading &&
          items.map((enrollment) => (
            <article key={enrollment.id} className="request-card">
              <div className="request-card__header">
                <h4>{enrollment.className}</h4>
                <span className="badge">{enrollment.status}</span>
              </div>
              <p className="hint">
                {enrollment.tutorName} • {enrollment.subjectName}
              </p>
              <p className="hint">
                {enrollment.scheduleDay} {enrollment.scheduleTime}
              </p>
              <p className="hint">Sessions attended: {enrollment.sessionsAttended}</p>
              {enrollment.status === "ACTIVE" && (
                <div className="request-actions">
                  <button
                    className="btn btn--ghost"
                    onClick={() => handleDrop(enrollment.id)}
                  >
                    Drop Class
                  </button>
                </div>
              )}
            </article>
          ))}
      </div>
      {!loading && !items.length && <p className="hint">No enrollments found.</p>}
    </div>
  );
};

export default Enrollments;
