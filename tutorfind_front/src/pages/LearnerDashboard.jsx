import { useEffect, useMemo, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const STATUS_LABELS = {
  PENDING: "Pending",
  ACCEPTED: "Booked",
  DECLINED: "Declined"
};

const LearnerDashboard = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [filter, setFilter] = useState("ALL");
  const [sessions, setSessions] = useState([]);
  const [feedback, setFeedback] = useState([]);
  const [reviewForm, setReviewForm] = useState({
    tutorId: "",
    rating: "",
    comment: "",
    bookingId: ""
  });
  const [reviewStatus, setReviewStatus] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadBookings = async (status = filter) => {
    if (!token || role !== "LEARNER") return;
    setLoading(true);
    setError("");
    try {
      const data = await api.bookingsSent(
        token,
        status === "ALL" ? undefined : status
      );
      setSessions(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Unable to load sessions");
    } finally {
      setLoading(false);
    }
  };

  const loadFeedback = async () => {
    if (!token || role !== "LEARNER") return;
    try {
      const data = await api.feedbackReceived(token);
      setFeedback(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load feedback", err);
    }
  };

  const submitReview = async (e) => {
    e.preventDefault();
    setReviewStatus("");
    if (!token) {
      setReviewStatus("Log in to leave a review.");
      return;
    }
    try {
      await api.createReview(token, {
        tutorId: reviewForm.tutorId ? Number(reviewForm.tutorId) : undefined,
        rating: reviewForm.rating ? Number(reviewForm.rating) : undefined,
        comment: reviewForm.comment,
        bookingId: reviewForm.bookingId
          ? Number(reviewForm.bookingId)
          : undefined
      });
      setReviewStatus("Review submitted.");
      setReviewForm({
        tutorId: "",
        rating: "",
        comment: "",
        bookingId: ""
      });
    } catch (err) {
      setReviewStatus(err.message || "Unable to submit review");
    }
  };

  useEffect(() => {
    loadBookings();
    loadFeedback();
  }, [token, role]);

  const filtered = useMemo(() => {
    if (filter === "ALL") return sessions;
    return sessions.filter((item) => item.status === filter);
  }, [filter, sessions]);

  const statusClass = (status) => {
    switch (status) {
      case "ACCEPTED":
        return "badge badge--green";
      case "PENDING":
        return "badge badge--yellow";
      case "DECLINED":
        return "badge badge--red";
      default:
        return "badge";
    }
  };

  if (role !== "LEARNER") {
    return (
      <div className="page-card">
        <p className="hint">Log in as a learner to see your sessions.</p>
      </div>
    );
  }

  return (
    <div className="dashboard dashboard--single">
      <section className="dashboard-content">
        <div className="card">
          <div className="requests-header">
            <div>
              <h3>My Sessions</h3>
              <p className="hint">
                View booked sessions or requests waiting for tutor confirmation.
              </p>
              {error && <p className="hint text-red-500">{error}</p>}
            </div>
            <div className="filter-tabs" role="tablist">
              {["ALL", "ACCEPTED", "PENDING", "DECLINED"].map((opt) => (
                <button
                  key={opt}
                  type="button"
                  className={`tab ${filter === opt ? "is-active" : ""}`}
                  onClick={() => {
                    setFilter(opt);
                    loadBookings(opt);
                  }}
                  role="tab"
                  aria-selected={filter === opt}
                >
                  {STATUS_LABELS[opt] || "All"}
                </button>
              ))}
            </div>
          </div>

          <div className="requests-list">
            {loading && <p className="hint">Loading sessions...</p>}
            {!loading &&
              filtered.map((session) => (
                <article key={session.id} className="request-card">
                  <div>
                    <div className="request-card__header">
                      <h4>{session.tutorName}</h4>
                      <span className={statusClass(session.status)}>
                        {STATUS_LABELS[session.status] || session.status}
                      </span>
                    </div>
                    <p className="hint">{session.subject}</p>
                    <p className="hint">{session.slot}</p>
                    {session.learnerNote && (
                      <p className="request-note">{session.learnerNote}</p>
                    )}
                  </div>
                </article>
              ))}
            {!loading && !filtered.length && (
              <p className="hint">No sessions in this view.</p>
            )}
          </div>
        </div>

        <div className="card mt-4">
          <h3>Leave a Review</h3>
          <p className="hint">
            Reviews are public and update tutor ratings. Booking ID is optional.
          </p>
          <form className="stacked-form" onSubmit={submitReview}>
            <div className="two-col">
              <label className="field">
                <span>Tutor ID</span>
                <input
                  name="tutorId"
                  value={reviewForm.tutorId}
                  onChange={(e) =>
                    setReviewForm((prev) => ({
                      ...prev,
                      tutorId: e.target.value
                    }))
                  }
                  required
                />
              </label>
              <label className="field">
                <span>Booking ID (optional)</span>
                <input
                  name="bookingId"
                  value={reviewForm.bookingId}
                  onChange={(e) =>
                    setReviewForm((prev) => ({
                      ...prev,
                      bookingId: e.target.value
                    }))
                  }
                />
              </label>
            </div>
            <label className="field">
              <span>Rating (1-5)</span>
              <input
                name="rating"
                type="number"
                min="1"
                max="5"
                value={reviewForm.rating}
                onChange={(e) =>
                  setReviewForm((prev) => ({
                    ...prev,
                    rating: e.target.value
                  }))
                }
                required
              />
            </label>
            <label className="field">
              <span>Comment</span>
              <textarea
                name="comment"
                rows="3"
                value={reviewForm.comment}
                onChange={(e) =>
                  setReviewForm((prev) => ({
                    ...prev,
                    comment: e.target.value
                  }))
                }
                required
              />
            </label>
            <button type="submit" className="btn btn--primary">
              Submit Review
            </button>
            {reviewStatus && <p className="hint">{reviewStatus}</p>}
          </form>
        </div>

        <div className="card mt-4">
          <h3>Feedback I Received</h3>
          {feedback.length ? (
            <div className="requests-list">
              {feedback.map((fb) => (
                <article key={fb.id} className="request-card">
                  <div className="request-card__header">
                    <h4>{fb.tutorName}</h4>
                    {fb.sessionDate && (
                      <span className="hint">{fb.sessionDate}</span>
                    )}
                  </div>
                  <p className="hint">{fb.subjectName}</p>
                  <p className="request-note">{fb.feedbackText}</p>
                  {fb.strengths && (
                    <p className="hint">Strengths: {fb.strengths}</p>
                  )}
                  {fb.areasForImprovement && (
                    <p className="hint">
                      Improve: {fb.areasForImprovement}
                    </p>
                  )}
                </article>
              ))}
            </div>
          ) : (
            <p className="hint">No feedback yet.</p>
          )}
        </div>
      </section>
    </div>
  );
};

export default LearnerDashboard;
