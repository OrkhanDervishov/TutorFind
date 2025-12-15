import { useEffect, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const DAYS = [
  { value: "MONDAY", label: "Monday" },
  { value: "TUESDAY", label: "Tuesday" },
  { value: "WEDNESDAY", label: "Wednesday" },
  { value: "THURSDAY", label: "Thursday" },
  { value: "FRIDAY", label: "Friday" },
  { value: "SATURDAY", label: "Saturday" },
  { value: "SUNDAY", label: "Sunday" }
];

const prettyDay = (d) => (d ? d[0] + d.slice(1).toLowerCase() : "");
const slotLabel = (slot) =>
  `${prettyDay(slot.dayOfWeek) || ""} ${slot.startTime || ""}${
    slot.endTime ? ` - ${slot.endTime}` : ""
  }`;

const emptyForm = {
  subjectId: "",
  name: "",
  description: "",
  classType: "INDIVIDUAL",
  maxStudents: 1,
  pricePerSession: "",
  totalSessions: "",
  durationMinutes: "",
  startDate: "",
  endDate: ""
};

const TutorClasses = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();

  const [form, setForm] = useState(emptyForm);
  const [selectedSlotId, setSelectedSlotId] = useState("");
  const [classes, setClasses] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [availability, setAvailability] = useState([]);
  const [roster, setRoster] = useState({});
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [newSlot, setNewSlot] = useState({
    dayOfWeek: "SUNDAY",
    startTime: "",
    endTime: ""
  });

  const loadSubjects = async () => {
    try {
      const list = await api.subjects();
      setSubjects(list);
    } catch {
      setSubjects([]);
    }
  };

  const loadAvailability = async () => {
    if (!token || role !== "TUTOR") return;
    try {
      const data = await api.myAvailability(token);
      const normalized = Array.isArray(data)
        ? data.map((s) => ({
            ...s,
            dayOfWeek: s.dayOfWeek ? s.dayOfWeek.toUpperCase() : s.dayOfWeek
          }))
        : [];
      setAvailability(normalized);
      if (!selectedSlotId && normalized.length) {
        setSelectedSlotId(String(normalized[0].id));
      }
    } catch {
      setAvailability([]);
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
    loadAvailability();
  }, [token, role]);

  const handleCreateClass = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    if (!selectedSlotId) {
      setError("Please select an availability slot.");
      return;
    }
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
          : undefined,
        availabilitySlotId: Number(selectedSlotId)
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

  const handleCreateSlot = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    if (!newSlot.startTime || !newSlot.endTime) {
      setError("Set start and end time to add a slot.");
      return;
    }
    try {
      const res = await api.addAvailability(token, {
        dayOfWeek: newSlot.dayOfWeek,
        startTime: newSlot.startTime,
        endTime: newSlot.endTime
      });
      setNewSlot({ dayOfWeek: "SUNDAY", startTime: "", endTime: "" });
      await loadAvailability();
      const createdId = res?.slotId || res?.id;
      if (createdId) {
        setSelectedSlotId(String(createdId));
      }
      setMessage("Availability slot added.");
    } catch (err) {
      setError(err.message || "Failed to add availability slot");
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

  const classSchedule = (c) => {
    const manual = `${c.scheduleDay || ""} ${c.scheduleTime || ""}`.trim();
    if (manual) {
      const day = manual.split(" ")[0];
      const pretty = prettyDay(day);
      return manual.replace(day, pretty).trim();
    }
    if (c.availabilitySlot) return slotLabel(c.availabilitySlot);
    return "Not scheduled";
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
        <form className="stacked-form" onSubmit={handleCreateClass}>
          <label className="field">
            <span>Availability slot</span>
            <select
              value={selectedSlotId}
              onChange={(e) => setSelectedSlotId(e.target.value)}
              required
            >
              <option value="">Select slot</option>
              {availability.map((slot) => (
                <option key={slot.id} value={slot.id}>
                  {slotLabel(slot)}
                </option>
              ))}
            </select>
          </label>

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
        <h3>Add an availability slot</h3>
        <p className="hint">Optional helper if you need a new slot before creating a class.</p>
        <form className="stacked-form" onSubmit={handleCreateSlot}>
          <div className="two-col">
            <label className="field">
              <span>Day</span>
              <select
                value={newSlot.dayOfWeek}
                onChange={(e) =>
                  setNewSlot((prev) => ({ ...prev, dayOfWeek: e.target.value }))
                }
              >
                {DAYS.map((d) => (
                  <option key={d.value} value={d.value}>
                    {d.label}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Start time</span>
              <input
                type="time"
                value={newSlot.startTime}
                onChange={(e) =>
                  setNewSlot((prev) => ({ ...prev, startTime: e.target.value }))
                }
                required
              />
            </label>
          </div>
          <div className="two-col">
            <label className="field">
              <span>End time</span>
              <input
                type="time"
                value={newSlot.endTime}
                onChange={(e) =>
                  setNewSlot((prev) => ({ ...prev, endTime: e.target.value }))
                }
                required
              />
            </label>
            <div className="field">
              <span>&nbsp;</span>
              <button type="submit" className="btn btn--ghost">
                + Slot
              </button>
            </div>
          </div>
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
                {classSchedule(c)} • {c.pricePerSession} AZN • Seats {c.currentStudents}/{c.maxStudents}
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
