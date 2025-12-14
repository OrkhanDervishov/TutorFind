import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const statusColors = {
  OPEN: "badge--green",
  FULL: "badge--red",
  IN_PROGRESS: "badge--yellow",
  COMPLETED: "badge",
  CANCELLED: "badge--red"
};

const ClassCard = ({ item, canEnroll, onEnroll }) => {
  return (
    <article className="tutor-card">
      <div className="tutor-card__header">
        <div>
          <h3>{item.name}</h3>
          <p className="hint">
            {item.subjectName} • {item.tutorName}
          </p>
          <p className="hint">
            {item.scheduleDay} {item.scheduleTime}
          </p>
        </div>
        <span className={`badge ${statusColors[item.status] || "badge"}`}>
          {item.status}
        </span>
      </div>
      <p className="tutor-card__subjects">
        Seats: {item.currentStudents}/{item.maxStudents} (avail:{" "}
        {item.availableSeats ?? item.maxStudents - item.currentStudents})
      </p>
      <div className="tutor-card__meta">
        <span className="price">
          {item.pricePerSession} AZN/session • {item.classType}
        </span>
        <Link className="btn btn--ghost" to={`/classes/${item.id}`}>
          View
        </Link>
      </div>
      {canEnroll && item.status === "OPEN" && (
        <button
          className="btn btn--primary btn--full mt-2"
          onClick={() => onEnroll(item.id)}
        >
          Enroll
        </button>
      )}
      {canEnroll && item.status !== "OPEN" && (
        <p className="hint mt-2">Enrollment closed.</p>
      )}
    </article>
  );
};

const Classes = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const isLearner = role === "LEARNER";
  const [classes, setClasses] = useState([]);
  const [statusFilter, setStatusFilter] = useState("OPEN");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadClasses = async (status = statusFilter) => {
    setLoading(true);
    setError("");
    try {
      const data = await api.listClasses(status === "ALL" ? undefined : status);
      setClasses(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Unable to load classes");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadClasses();
  }, []);

  const handleEnroll = async (classId) => {
    if (!token || !isLearner) {
      setMessage("Log in as a learner to enroll.");
      return;
    }
    try {
      await api.enrollInClass(token, classId);
      setMessage("Enrolled successfully.");
      loadClasses();
    } catch (err) {
      setMessage(err.message || "Enrollment failed");
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Classes</p>
          <h1>Browse classes</h1>
          <p className="hint">
            Find group sessions and enroll if seats are available.
          </p>
          {message && <p className="hint">{message}</p>}
          {error && <p className="hint text-red-500">{error}</p>}
        </div>
        <label className="field field--inline">
          <span>Status</span>
          <select
            value={statusFilter}
            onChange={(e) => {
              const value = e.target.value;
              setStatusFilter(value);
              loadClasses(value);
            }}
          >
            <option value="OPEN">Open</option>
            <option value="ALL">All</option>
            <option value="FULL">Full</option>
            <option value="IN_PROGRESS">In progress</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </label>
      </div>

      {loading && <p className="hint">Loading classes...</p>}
      <div className="results-grid">
        {!loading &&
          classes.map((item) => (
            <ClassCard
              key={item.id}
              item={item}
              canEnroll={isLearner}
              onEnroll={handleEnroll}
            />
          ))}
      </div>
      {!loading && !classes.length && <p className="hint">No classes found.</p>}
    </div>
  );
};

export default Classes;
