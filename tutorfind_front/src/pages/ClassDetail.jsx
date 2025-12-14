import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const ClassDetail = () => {
  const { id } = useParams();
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const isLearner = role === "LEARNER";
  const isTutor = role === "TUTOR";
  const [item, setItem] = useState(null);
  const [roster, setRoster] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadClass = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await api.classById(id);
      setItem(data);
      if (isTutor) {
        // try roster, ignore failure for non-owners
        try {
          const r = await api.classRoster(token, id);
          setRoster(Array.isArray(r) ? r : []);
        } catch {
          setRoster([]);
        }
      }
    } catch (err) {
      setError(err.message || "Unable to load class");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadClass();
  }, [id]);

  const handleEnroll = async () => {
    if (!token || !isLearner) {
      setMessage("Log in as a learner to enroll.");
      return;
    }
    try {
      await api.enrollInClass(token, id);
      setMessage("Enrolled successfully.");
      loadClass();
    } catch (err) {
      setMessage(err.message || "Enrollment failed");
    }
  };

  if (loading) return <p className="hint">Loading class...</p>;
  if (error || !item) return <p className="hint text-red-500">{error || "Not found"}</p>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Class</p>
          <h1>{item.name}</h1>
          <p className="hint">
            {item.subjectName} • {item.tutorName}
          </p>
          <p className="hint">
            {item.scheduleDay} {item.scheduleTime} • {item.durationMinutes} mins
          </p>
          <p className="hint">
            Seats: {item.currentStudents}/{item.maxStudents} (avail:{" "}
            {item.availableSeats ?? item.maxStudents - item.currentStudents}) • Status:{" "}
            {item.status}
          </p>
          {message && <p className="hint">{message}</p>}
        </div>
        {isLearner && (
          <button
            className="btn btn--primary"
            onClick={handleEnroll}
            disabled={item.status !== "OPEN"}
          >
            Enroll
          </button>
        )}
      </div>

      <div className="card">
        <h3>Description</h3>
        <p>{item.description || "No description provided."}</p>
      </div>

      <div className="card">
        <h3>Details</h3>
        <ul className="list">
          <li>Class type: {item.classType}</li>
          <li>Price per session: {item.pricePerSession} AZN</li>
          <li>Total sessions: {item.totalSessions}</li>
          <li>
            Dates: {item.startDate} - {item.endDate}
          </li>
        </ul>
      </div>

      {isTutor && roster.length > 0 && (
        <div className="card">
          <h3>Roster</h3>
          <div className="requests-list">
            {roster.map((row) => (
              <article key={row.id} className="request-card">
                <div className="request-card__header">
                  <h4>{row.learnerName}</h4>
                  <span className="badge badge--green">{row.status}</span>
                </div>
                <p className="hint">Enrolled: {row.enrollmentDate}</p>
                <p className="hint">Sessions attended: {row.sessionsAttended}</p>
                <p className="hint">Payment: {row.paymentStatus}</p>
              </article>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ClassDetail;
