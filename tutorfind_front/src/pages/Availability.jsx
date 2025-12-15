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

const Availability = () => {
  const { token, user } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [slots, setSlots] = useState([]);
  const [newSlot, setNewSlot] = useState({
    dayOfWeek: "SUNDAY",
    startTime: "",
    endTime: ""
  });
  const [status, setStatus] = useState("");

  const loadAvailability = async () => {
    if (!token || role !== "TUTOR") return;
    try {
      const data = await api.myAvailability(token);
      const mapped = (data || []).map((slot) => ({
        id: slot.id,
        dayOfWeek: slot.dayOfWeek ? slot.dayOfWeek.toUpperCase() : slot.day,
        startTime: slot.startTime,
        endTime: slot.endTime
      }));
      setSlots(mapped);
    } catch (err) {
      setStatus(err.message || "Failed to load availability");
    }
  };

  useEffect(() => {
    loadAvailability();
  }, [token, role]);

  const handleAddSlot = async (e) => {
    e.preventDefault();
    setStatus("");
    if (!token) {
      setStatus("Log in to manage availability.");
      return;
    }
    if (!newSlot.startTime || !newSlot.endTime) {
      setStatus("Please provide start and end time.");
      return;
    }
    try {
      await api.addAvailability(token, {
        dayOfWeek: newSlot.dayOfWeek,
        startTime: newSlot.startTime,
        endTime: newSlot.endTime
      });
      setNewSlot((prev) => ({ ...prev, startTime: "", endTime: "" }));
      setStatus("Availability slot added.");
      loadAvailability();
    } catch (err) {
      setStatus(err.message || "Unable to add slot");
    }
  };

  const handleDeleteSlot = async (slot) => {
    setStatus("");
    if (!token) {
      setStatus("Log in to manage availability.");
      return;
    }
    if (!slot.id) {
      setStatus("Slot id missing; cannot delete.");
      return;
    }
    try {
      await api.removeAvailability(token, slot.id);
      setSlots((prev) => prev.filter((item) => item.id !== slot.id));
      setStatus("Slot removed.");
    } catch (err) {
      setStatus(err.message || "Unable to remove slot");
    }
  };

  if (role !== "TUTOR") {
    return (
      <div className="page-card">
        <p className="hint">Availability management is for tutors only.</p>
      </div>
    );
  }

  return (
    <div className="page-narrow">
      <div className="card">
        <h2>Manage Availability</h2>
        <p className="hint">
          Add recurring weekly slots so learners can request times that work for you.
        </p>
        {status && <p className="hint">{status}</p>}
        <form className="slot-form" onSubmit={handleAddSlot}>
          <label className="field">
            <span>Day</span>
            <select
              name="dayOfWeek"
              value={newSlot.dayOfWeek}
              onChange={(e) =>
                setNewSlot((prev) => ({ ...prev, dayOfWeek: e.target.value }))
              }
            >
              {DAYS.map((day) => (
                <option key={day.value} value={day.value}>
                  {day.label}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>Start</span>
            <input
              type="time"
              value={newSlot.startTime}
              onChange={(e) =>
                setNewSlot((prev) => ({ ...prev, startTime: e.target.value }))
              }
            />
          </label>
          <label className="field">
            <span>End</span>
            <input
              type="time"
              value={newSlot.endTime}
              onChange={(e) =>
                setNewSlot((prev) => ({ ...prev, endTime: e.target.value }))
              }
            />
          </label>
          <button type="submit" className="btn btn--primary">
            Add Slot
          </button>
        </form>

        <div className="slots-list">
          {slots.map((slot) => (
            <div key={`${slot.id || ""}-${slot.dayOfWeek}-${slot.startTime}-${slot.endTime}`} className="slot-row">
              <span>
                {prettyDay(slot.dayOfWeek)}: {slot.startTime} - {slot.endTime}
              </span>
              <button
                type="button"
                className="icon-button"
                onClick={() => handleDeleteSlot(slot)}
                aria-label="Delete slot"
              >
                X
              </button>
            </div>
          ))}
          {!slots.length && (
            <p className="hint">No availability yet. Add your first slot above.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Availability;
