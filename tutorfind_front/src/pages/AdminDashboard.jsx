import { useEffect, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const AdminDashboard = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const isAdmin = role === "ADMIN";

  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [unverified, setUnverified] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadAll = async () => {
    if (!isAdmin || !token) return;
    setError("");
    try {
      const [s, u, t, r] = await Promise.all([
        api.adminStats(token),
        api.adminUsers(token),
        api.adminUnverifiedTutors(token),
        api.adminPendingReviews(token)
      ]);
      setStats(s);
      setUsers(Array.isArray(u) ? u : []);
      setUnverified(Array.isArray(t) ? t : []);
      setReviews(Array.isArray(r) ? r : []);
    } catch (err) {
      setError(err.message || "Failed to load admin data");
    }
  };

  useEffect(() => {
    loadAll();
  }, [token, role]);

  const handleVerify = async (id, action) => {
    setMessage("");
    setError("");
    try {
      if (action === "verify") {
        await api.adminVerifyTutor(token, id);
      } else {
        await api.adminUnverifyTutor(token, id);
      }
      setMessage("Tutor updated.");
      loadAll();
    } catch (err) {
      setError(err.message || "Tutor update failed");
    }
  };

  const handleReview = async (id, action) => {
    setMessage("");
    setError("");
    try {
      if (action === "approve") {
        await api.adminApproveReview(token, id);
      } else {
        await api.adminRejectReview(token, id, "Rejected by admin");
      }
      setMessage("Review updated.");
      loadAll();
    } catch (err) {
      setError(err.message || "Review update failed");
    }
  };

  const handleUserStatus = async (id, action) => {
    setMessage("");
    setError("");
    try {
      if (action === "deactivate") {
        await api.adminDeactivateUser(token, id);
      } else {
        await api.adminActivateUser(token, id);
      }
      setMessage("User updated.");
      loadAll();
    } catch (err) {
      setError(err.message || "User update failed");
    }
  };

  if (!isAdmin) {
    return <p className="hint">Admin access only.</p>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Admin</p>
          <h1>Admin Console</h1>
          {message && <p className="hint">{message}</p>}
          {error && <p className="hint text-red-500">{error}</p>}
        </div>
      </div>

      {stats && (
        <div className="results-grid">
          <div className="card">
            <h3>Users</h3>
            <p className="hint">Total: {stats.totalUsers}</p>
            <p className="hint">Tutors: {stats.totalTutors}</p>
            <p className="hint">Learners: {stats.totalLearners}</p>
          </div>
          <div className="card">
            <h3>Tutor Verification</h3>
            <p className="hint">Verified: {stats.verifiedTutors}</p>
            <p className="hint">Unverified: {stats.unverifiedTutors}</p>
          </div>
          <div className="card">
            <h3>Reviews</h3>
            <p className="hint">Total: {stats.totalReviews}</p>
            <p className="hint">Pending: {stats.pendingReviews}</p>
          </div>
          <div className="card">
            <h3>Bookings</h3>
            <p className="hint">Total: {stats.totalBookings}</p>
            <p className="hint">Pending: {stats.pendingBookings}</p>
          </div>
          <div className="card">
            <h3>Classes</h3>
            <p className="hint">Total: {stats.totalClasses}</p>
            <p className="hint">Open: {stats.openClasses}</p>
            <p className="hint">Full: {stats.fullClasses}</p>
          </div>
        </div>
      )}

      <div className="card mt-4">
        <h3>Unverified Tutors</h3>
        {unverified.length ? (
          <div className="requests-list">
            {unverified.map((t) => (
              <article key={t.id} className="request-card">
                <div className="request-card__header">
                  <h4>{t.headline || `Tutor ${t.userId}`}</h4>
                  <span className="hint">Experience: {t.experienceYears || 0} yrs</span>
                </div>
                <p className="hint">Created: {t.createdAt}</p>
                <div className="request-actions">
                  <button
                    className="btn btn--primary"
                    onClick={() => handleVerify(t.id, "verify")}
                  >
                    Verify
                  </button>
                  <button
                    className="btn btn--ghost"
                    onClick={() => handleVerify(t.id, "unverify")}
                  >
                    Keep Unverified
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="hint">No tutors awaiting verification.</p>
        )}
      </div>

      <div className="card mt-4">
        <h3>Pending Reviews</h3>
        {reviews.length ? (
          <div className="requests-list">
            {reviews.map((r) => (
              <article key={r.id} className="request-card">
                <div className="request-card__header">
                  <h4>{r.comment || "Review"}</h4>
                  <span className="badge">{r.status}</span>
                </div>
                <p className="hint">
                  Tutor {r.tutorId} • Learner {r.learnerId} • Rating {r.rating}
                </p>
                <div className="request-actions">
                  <button
                    className="btn btn--primary"
                    onClick={() => handleReview(r.id, "approve")}
                  >
                    Approve
                  </button>
                  <button
                    className="btn btn--ghost"
                    onClick={() => handleReview(r.id, "reject")}
                  >
                    Reject
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="hint">No pending reviews.</p>
        )}
      </div>

      <div className="card mt-4">
        <h3>User Management</h3>
        {users.length ? (
          <div className="requests-list">
            {users.map((u) => (
              <article key={u.id} className="request-card">
                <div className="request-card__header">
                  <h4>
                    {u.firstName} {u.lastName}
                  </h4>
                  <span className="hint">{u.email}</span>
                </div>
                <p className="hint">Role: {u.role}</p>
                <div className="request-actions">
                  <button
                    className="btn btn--ghost"
                    onClick={() =>
                      handleUserStatus(
                        u.id,
                        u.isActive === false ? "activate" : "deactivate"
                      )
                    }
                  >
                    {u.isActive === false ? "Activate" : "Deactivate"}
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <p className="hint">No users found.</p>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
