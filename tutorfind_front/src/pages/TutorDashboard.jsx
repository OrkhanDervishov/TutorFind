import { useEffect, useMemo, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const STATUS_LABELS = {
  PENDING: "Pending",
  ACCEPTED: "Accepted",
  DECLINED: "Declined"
};

const TutorDashboard = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [acceptedSlotFilter, setAcceptedSlotFilter] = useState("all");
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [feedbackGiven, setFeedbackGiven] = useState([]);
  const [feedbackForm, setFeedbackForm] = useState({
    learnerId: "",
    bookingId: "",
    subjectId: "",
    sessionDate: "",
    feedbackText: "",
    strengths: "",
    areasForImprovement: ""
  });

  const loadBookings = async (status = statusFilter) => {
    if (!token || role !== "TUTOR") return;
    setLoading(true);
    setError("");
    try {
      const bookings = await api.bookingsReceived(
        token,
        status === "ALL" ? undefined : status
      );
      setRequests(Array.isArray(bookings) ? bookings : []);
    } catch (err) {
      setError(err.message || "Unable to load bookings");
    } finally {
      setLoading(false);
    }
  };

  const loadFeedback = async () => {
    if (!token || role !== "TUTOR") return;
    try {
      const data = await api.feedbackGiven(token);
      setFeedbackGiven(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load feedback", err);
    }
  };

  const submitFeedback = async (e) => {
    e.preventDefault();
    if (!token) return;
    setError("");
    try {
      await api.createFeedback(token, {
        learnerId: feedbackForm.learnerId
          ? Number(feedbackForm.learnerId)
          : undefined,
        bookingId: feedbackForm.bookingId
          ? Number(feedbackForm.bookingId)
          : undefined,
        subjectId: feedbackForm.subjectId
          ? Number(feedbackForm.subjectId)
          : undefined,
        sessionDate: feedbackForm.sessionDate || undefined,
        feedbackText: feedbackForm.feedbackText,
        strengths: feedbackForm.strengths || undefined,
        areasForImprovement: feedbackForm.areasForImprovement || undefined
      });
      setFeedbackForm({
        learnerId: "",
        bookingId: "",
        subjectId: "",
        sessionDate: "",
        feedbackText: "",
        strengths: "",
        areasForImprovement: ""
      });
      loadFeedback();
    } catch (err) {
      setError(err.message || "Unable to submit feedback");
    }
  };

  useEffect(() => {
    loadBookings();
    loadFeedback();
  }, [token, role]);

  const handleAction = async (id, action) => {
    if (!token) return;
    try {
      const responseMessage =
        action === "decline"
          ? window.prompt("Optional response to learner?")
          : undefined;
      const updated =
        action === "accept"
          ? await api.acceptBooking(token, id, responseMessage)
          : await api.declineBooking(token, id, responseMessage);

      setRequests((prev) =>
        prev.map((item) => (item.id === id ? updated : item))
      );
    } catch (err) {
      setError(err.message || "Failed to update booking");
    }
  };

  const filtered = useMemo(() => {
    let list = requests;
    if (statusFilter !== "ALL") {
      list = list.filter((item) => item.status === statusFilter);
    }
    if (statusFilter === "ACCEPTED" && acceptedSlotFilter !== "all") {
      list = list.filter((item) =>
        (item.slot || "").includes(acceptedSlotFilter)
      );
    }
    return list;
  }, [requests, statusFilter, acceptedSlotFilter]);

  const acceptedSlots = useMemo(() => {
    return Array.from(
      new Set(
        requests
          .filter((r) => r.status === "ACCEPTED" && r.slot)
          .map((r) => r.slot)
      )
    );
  }, [requests]);

  if (role !== "TUTOR") {
    return (
      <div className="page-card">
        <p className="hint">
          Log in as a tutor to view and respond to booking requests.
        </p>
      </div>
    );
  }

  return (
    <div className="dashboard dashboard--single">
      <section className="dashboard-content">
        <div className="card">
          <div className="requests-header">
            <div>
              <h3>Booking Requests</h3>
              <p className="hint">
                Choose a view: all, pending, or accepted.
              </p>
              {error && <p className="hint text-red-500">{error}</p>}
            </div>
            <div className="flex flex-wrap gap-3 items-center">
              <div className="filter-tabs" role="tablist">
                {["ALL", "PENDING", "ACCEPTED", "DECLINED"].map((opt) => (
                  <button
                    key={opt}
                    type="button"
                    className={`tab ${statusFilter === opt ? "is-active" : ""}`}
                    onClick={() => {
                      setStatusFilter(opt);
                      loadBookings(opt);
                    }}
                    role="tab"
                    aria-selected={statusFilter === opt}
                  >
                    {STATUS_LABELS[opt] || "All"}
                  </button>
                ))}
              </div>
              {statusFilter === "ACCEPTED" && (
                <label className="field field--inline">
                  <span>Filter by slot</span>
                  <select
                    value={acceptedSlotFilter}
                    onChange={(e) => setAcceptedSlotFilter(e.target.value)}
                  >
                    <option value="all">All slots</option>
                    {acceptedSlots.map((slot) => (
                      <option key={slot} value={slot}>
                        {slot}
                      </option>
                    ))}
                  </select>
                </label>
              )}
            </div>
          </div>

          <div className="requests-list">
            {loading && <p className="hint">Loading requests...</p>}
            {!loading &&
              filtered.map((req) => (
                <article key={req.id} className="request-card">
                  <div>
                    <div className="request-card__header">
                      <h4>{req.learnerName || "Learner"}</h4>
                      <span
                        className={`badge ${
                          req.status === "ACCEPTED"
                            ? "badge--green"
                            : req.status === "DECLINED"
                            ? "badge--red"
                            : "badge--yellow"
                        }`}
                      >
                        {STATUS_LABELS[req.status] || req.status}
                      </span>
                    </div>
                    <p className="hint">{req.subject}</p>
                    <p className="hint">{req.slot}</p>
                    {req.learnerPhone && (
                      <p className="hint">Phone: {req.learnerPhone}</p>
                    )}
                    {req.learnerNote && (
                      <p className="request-note">{req.learnerNote}</p>
                    )}
                  </div>
                  {req.status === "PENDING" && (
                    <div className="request-actions">
                      <button
                        className="btn btn--ghost"
                        onClick={() => handleAction(req.id, "decline")}
                      >
                        Decline
                      </button>
                      <button
                        className="btn btn--primary"
                        onClick={() => handleAction(req.id, "accept")}
                      >
                        Accept
                      </button>
                    </div>
                  )}
                </article>
              ))}
            {!loading && !filtered.length && (
              <p className="hint">No requests in this view.</p>
            )}
          </div>
        </div>

        <div className="card mt-4">
          <h3>Feedback I Gave</h3>
          <p className="hint">
            Submit progress feedback to learners. Only the learner and you can view it.
          </p>
          <form className="stacked-form" onSubmit={submitFeedback}>
            <div className="two-col">
              <label className="field">
                <span>Learner ID</span>
                <input
                  name="learnerId"
                  value={feedbackForm.learnerId}
                  onChange={(e) =>
                    setFeedbackForm((prev) => ({
                      ...prev,
                      learnerId: e.target.value
                    }))
                  }
                  required
                />
              </label>
              <label className="field">
                <span>Booking ID (optional)</span>
                <input
                  name="bookingId"
                  value={feedbackForm.bookingId}
                  onChange={(e) =>
                    setFeedbackForm((prev) => ({
                      ...prev,
                      bookingId: e.target.value
                    }))
                  }
                />
              </label>
            </div>
            <div className="two-col">
              <label className="field">
                <span>Subject ID (optional)</span>
                <input
                  name="subjectId"
                  value={feedbackForm.subjectId}
                  onChange={(e) =>
                    setFeedbackForm((prev) => ({
                      ...prev,
                      subjectId: e.target.value
                    }))
                  }
                />
              </label>
              <label className="field">
                <span>Session Date</span>
                <input
                  type="date"
                  name="sessionDate"
                  value={feedbackForm.sessionDate}
                  onChange={(e) =>
                    setFeedbackForm((prev) => ({
                      ...prev,
                      sessionDate: e.target.value
                    }))
                  }
                />
              </label>
            </div>
            <label className="field">
              <span>Feedback</span>
              <textarea
                name="feedbackText"
                rows="3"
                value={feedbackForm.feedbackText}
                onChange={(e) =>
                  setFeedbackForm((prev) => ({
                    ...prev,
                    feedbackText: e.target.value
                  }))
                }
                required
              />
            </label>
            <label className="field">
              <span>Strengths</span>
              <input
                name="strengths"
                value={feedbackForm.strengths}
                onChange={(e) =>
                  setFeedbackForm((prev) => ({
                    ...prev,
                    strengths: e.target.value
                  }))
                }
              />
            </label>
            <label className="field">
              <span>Areas for improvement</span>
              <input
                name="areasForImprovement"
                value={feedbackForm.areasForImprovement}
                onChange={(e) =>
                  setFeedbackForm((prev) => ({
                    ...prev,
                    areasForImprovement: e.target.value
                  }))
                }
              />
            </label>
            <button type="submit" className="btn btn--primary">
              Submit Feedback
            </button>
          </form>
          {feedbackGiven.length ? (
            <div className="requests-list">
              {feedbackGiven.map((fb) => (
                <article key={fb.id} className="request-card">
                  <div className="request-card__header">
                    <h4>{fb.learnerName}</h4>
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
            <p className="hint">No feedback shared yet.</p>
          )}
        </div>
      </section>
    </div>
  );
};

export default TutorDashboard;
