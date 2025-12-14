import { useEffect, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const emptyForm = {
  subjectId: "",
  name: "",
  description: "",
  classType: "INDIVIDUAL",
  maxStudents: 1,
  pricePerSession: "",
  totalSessions: "",
  scheduleDay: "Monday",
  scheduleTime: "",
  durationMinutes: "",
  startDate: "",
  endDate: ""
};

const TutorClasses = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [form, setForm] = useState(emptyForm);
  const [classes, setClasses] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [roster, setRoster] = useState({});
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const loadSubjects = async () => {
    try {
      const list = await api.subjects();
      setSubjects(list);
    } catch {
      setSubjects([]);
    }
  };

  const loadClasses = async () => {
    if (!token || role !== "TUTOR") return;
    setLoading(true);
    setError("");
    try {
      const data = await api.myClasses(token);
      setClasses(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Unable to load classes");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSubjects();
    loadClasses();
  }, [token, role]);

  const handleCreate = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    try {
      await api.createClass(token, {
        ...form,
        subjectId: form.subjectId ? Number(form.subjectId) : undefined,
        maxStudents: Number(form.maxStudents || 1),
        pricePerSession: form.pricePerSession
          ? Number(form.pricePerSession)
          : undefined,
        totalSessions: form.totalSessions
          ? Number(form.totalSessions)
          : undefined,
        durationMinutes: form.durationMinutes
          ? Number(form.durationMinutes)
          : undefined
      });
      setMessage("Class created.");
      setForm(emptyForm);
      loadClasses();
    } catch (err) {
      setError(err.message || "Failed to create class");
    }
  };

  const handleDelete = async (id) => {
    setMessage("");
    setError("");
    try {
      await api.deleteClass(token, id);
      setMessage("Class deleted.");
      loadClasses();
    } catch (err) {
      setError(err.message || "Delete failed");
    }
  };

  const handleUpdate = async (id, patch) => {
    setMessage("");
    setError("");
    try {
      await api.updateClass(token, id, patch);
      setMessage("Class updated.");
      loadClasses();
    } catch (err) {
      setError(err.message || "Update failed");
    }
  };

  const toggleRoster = async (id) => {
    if (roster[id]) {
      setRoster((prev) => {
        const copy = { ...prev };
        delete copy[id];
        return copy;
      });
      return;
    }
    try {
      const data = await api.classRoster(token, id);
      setRoster((prev) => ({ ...prev, [id]: Array.isArray(data) ? data : [] }));
    } catch (err) {
      setError(err.message || "Failed to load roster");
    }
  };

  if (role !== "TUTOR") {
    return <p className="hint">Tutor login required to manage classes.</p>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Tutor</p>
          <h1>My Classes</h1>
          {message && <p className="hint">{message}</p>}
          {error && <p className="hint text-red-500">{error}</p>}
        </div>
      </div>

      <div className="card">
        <h3>Create a Class</h3>
        <form className="stacked-form" onSubmit={handleCreate}>
          <div className="two-col">
            <label className="field">
              <span>Subject</span>
              <select
                value={form.subjectId}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, subjectId: e.target.value }))
                }
                required
              >
                <option value="">Select subject</option>
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Class type</span>
              <select
                value={form.classType}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, classType: e.target.value }))
                }
              >
                <option value="INDIVIDUAL">INDIVIDUAL</option>
                <option value="SMALL_GROUP">SMALL_GROUP</option>
                <option value="LARGE_GROUP">LARGE_GROUP</option>
              </select>
            </label>
          </div>
          <label className="field">
            <span>Name</span>
            <input
              value={form.name}
              onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))}
              required
            />
          </label>
          <label className="field">
            <span>Description</span>
            <textarea
              rows="3"
              value={form.description}
              onChange={(e) =>
                setForm((prev) => ({ ...prev, description: e.target.value }))
              }
            />
          </label>
          <div className="two-col">
            <label className="field">
              <span>Max students</span>
              <input
                type="number"
                min="1"
                value={form.maxStudents}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, maxStudents: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>Price per session (AZN)</span>
              <input
                type="number"
                min="0"
                value={form.pricePerSession}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, pricePerSession: e.target.value }))
                }
              />
            </label>
          </div>
          <div className="two-col">
            <label className="field">
              <span>Total sessions</span>
              <input
                type="number"
                min="1"
                value={form.totalSessions}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, totalSessions: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>Duration (minutes)</span>
              <input
                type="number"
                min="30"
                value={form.durationMinutes}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, durationMinutes: e.target.value }))
                }
              />
            </label>
          </div>
          <div className="two-col">
            <label className="field">
              <span>Schedule day</span>
              <input
                value={form.scheduleDay}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, scheduleDay: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>Schedule time</span>
              <input
                value={form.scheduleTime}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, scheduleTime: e.target.value }))
                }
                placeholder="e.g., 18:00-20:00"
              />
            </label>
          </div>
          <div className="two-col">
            <label className="field">
              <span>Start date</span>
              <input
                type="date"
                value={form.startDate}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, startDate: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>End date</span>
              <input
                type="date"
                value={form.endDate}
                onChange={(e) =>
                  setForm((prev) => ({ ...prev, endDate: e.target.value }))
                }
              />
            </label>
          </div>
          <button type="submit" className="btn btn--primary">
            Create Class
          </button>
        </form>
      </div>

      <div className="card">
        <h3>My Classes</h3>
        {loading && <p className="hint">Loading...</p>}
        {!loading && !classes.length && <p className="hint">No classes yet.</p>}
        <div className="requests-list">
          {classes.map((c) => (
            <article key={c.id} className="request-card">
              <div className="request-card__header">
                <h4>{c.name}</h4>
                <span className="badge">{c.status}</span>
              </div>
              <p className="hint">
                {c.scheduleDay} {c.scheduleTime} • {c.pricePerSession} AZN • Seats{" "}
                {c.currentStudents}/{c.maxStudents}
              </p>
              <p className="hint">{c.description}</p>
              <div className="request-actions">
                <button
                  className="btn btn--ghost"
                  onClick={() =>
                    handleUpdate(c.id, { maxStudents: c.maxStudents + 1 })
                  }
                >
                  +1 seat
                </button>
                <button
                  className="btn btn--ghost"
                  onClick={() => toggleRoster(c.id)}
                >
                  {roster[c.id] ? "Hide roster" : "View roster"}
                </button>
                <button
                  className="btn btn--ghost"
                  onClick={() =>
                    handleUpdate(c.id, { pricePerSession: c.pricePerSession })
                  }
                >
                  Refresh
                </button>
                <button
                  className="btn btn--primary"
                  onClick={() => handleDelete(c.id)}
                >
                  Delete
                </button>
              </div>
              {roster[c.id] && (
                <div className="mt-2">
                  {roster[c.id].length ? (
                    roster[c.id].map((r) => (
                      <p key={r.id} className="hint">
                        {r.learnerName} • {r.status} • {r.paymentStatus}
                      </p>
                    ))
                  ) : (
                    <p className="hint">No enrollments yet.</p>
                  )}
                </div>
              )}
            </article>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TutorClasses;
