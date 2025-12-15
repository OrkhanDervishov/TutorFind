import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const TutorProfile = () => {
  const { id } = useParams();
  const { user, token } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const isLearner = role === "LEARNER";

  const [profile, setProfile] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [booking, setBooking] = useState({
    subjectId: "",
    subject: "",
    mode: "online",
    slot: "",
    note: "",
    proposedPrice: ""
  });
  const [bookingResult, setBookingResult] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError("");
        const [profileData, reviewData] = await Promise.all([
          api.tutorProfile(id),
          api.tutorReviews(id)
        ]);
        setProfile(profileData);
        setReviews(Array.isArray(reviewData) ? reviewData : []);

        if (profileData?.subjects?.length) {
          setBooking((prev) => ({
            ...prev,
            subjectId: profileData.subjects[0].id,
            subject: profileData.subjects[0].name
          }));
        }
        if (profileData?.availability?.length) {
          const firstSlot = `${profileData.availability[0].day} ${profileData.availability[0].startTime}-${profileData.availability[0].endTime}`;
          setBooking((prev) => ({ ...prev, slot: firstSlot }));
        }
        if (profileData?.monthlyRate) {
          setBooking((prev) => ({
            ...prev,
            proposedPrice: profileData.monthlyRate
          }));
        }
      } catch (err) {
        setError(err.message || "Unable to load tutor profile");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const availabilitySlots = useMemo(() => {
    if (!profile?.availability) return [];
    return profile.availability.map(
      (slot) => `${slot.day} ${slot.startTime}-${slot.endTime}`
    );
  }, [profile]);

  const handleBookingChange = (e) => {
    const { name, value } = e.target;
    setBooking((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setBookingResult("");
    if (!token || !isLearner) {
      setBookingResult("Please log in as a learner to book.");
      return;
    }
    try {
      await api.createBooking(token, {
        tutorId: Number(id),
        subjectId: booking.subjectId ? Number(booking.subjectId) : undefined,
        subject: booking.subject || undefined,
        mode: booking.mode,
        slot: booking.slot,
        note: booking.note,
        proposedPrice: booking.proposedPrice || undefined
      });
      setBookingResult("Booking request sent!");
      setBooking((prev) => ({ ...prev, note: "" }));
    } catch (err) {
      setBookingResult(err.message || "Failed to send booking");
    }
  };

  if (loading) {
    return (
      <div className="page-card">
        <p>Loading tutor profile...</p>
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div className="page-card">
        <p className="hint text-red-500">{error || "Tutor not found"}</p>
      </div>
    );
  }

  const fullName =
    [profile.firstName, profile.lastName].filter(Boolean).join(" ") ||
    "Tutor";

  return (
    <div className="profile-page">
      <header className="profile-header">
        <div className="profile-header__left">
          <div className="avatar profile-photo">{fullName[0]}</div>
          <div>
            <p className="eyebrow">Tutor profile</p>
            <h1>{fullName}</h1>
            {profile.headline && <p className="hint">{profile.headline}</p>}
          </div>
        </div>
        <div className="profile-header__right">
          <div className="profile-stat">
            <span className="label">Rate</span>
            <strong>
              {profile.monthlyRate !== null && profile.monthlyRate !== undefined
                ? `${profile.monthlyRate} AZN/month`
                : "Not set"}
            </strong>
          </div>
          <div className="profile-stat">
            <span className="label">Rating</span>
            <strong>
              {profile.rating !== null && profile.rating !== undefined ? (
                <>
                  Rating {Number(profile.rating).toFixed(1)}{" "}
                  <span className="hint">
                    ({profile.reviewCount || 0} reviews)
                  </span>
                </>
              ) : (
                <span className="hint">No rating yet</span>
              )}
            </strong>
          </div>
          <div className="profile-stat">
            <span className="label">Location</span>
            <strong>
              {profile.city}
              {profile.districts?.length
                ? ` • ${profile.districts.join(", ")}`
                : ""}
            </strong>
          </div>
          <div className="profile-stat">
            <span className="label">Phone</span>
            <strong>{profile.phoneNumber || "Not shared"}</strong>
          </div>
        </div>
      </header>

      <section className="profile-main">
        <div className="profile-details">
          <div className="card">
            <h2>About me</h2>
            <p>{profile.bio || "No bio added yet."}</p>
          </div>

          {profile.qualifications && (
            <div className="card">
              <h3>Qualifications</h3>
              <ul className="list">
                {profile.qualifications.split("\n").map((item) => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            </div>
          )}

          {profile.subjects?.length ? (
            <div className="card">
              <h3>Subjects</h3>
              <div className="tags">
                {profile.subjects.map((subject) => (
                  <span key={subject.id} className="chip">
                    {subject.name}
                  </span>
                ))}
              </div>
            </div>
          ) : null}

          <div className="card">
            <h3>Reviews</h3>
            <div className="reviews">
              {reviews.length ? (
                reviews.map((review) => (
                  <div key={review.id} className="review">
                    <div className="review__header">
                      <strong>{review.learnerName}</strong>
                      <span className="rating">Rating {review.rating}</span>
                    </div>
                    <p className="review__comment">{review.comment}</p>
                  </div>
                ))
              ) : (
                <p className="hint">No reviews yet.</p>
              )}
            </div>
          </div>
        </div>

        <aside className="booking-column">
          <div className="card booking-card">
            <h3>Book a Session</h3>
            <p className="hint">
              Select a weekly slot and send a booking request.
            </p>

            <form className="stacked-form" onSubmit={handleSubmit}>
              <label className="field">
                <span>Select an available time</span>
                <select
                  name="slot"
                  value={booking.slot}
                  onChange={handleBookingChange}
                  required
                  disabled={!availabilitySlots.length}
                >
                  {!availabilitySlots.length && (
                    <option value="">No availability listed</option>
                  )}
                  {availabilitySlots.map((slot) => (
                    <option key={slot} value={slot}>
                      {slot}
                    </option>
                  ))}
                </select>
              </label>

              <label className="field">
                <span>Subject</span>
                <select
                  name="subjectId"
                  value={booking.subjectId}
                  onChange={(e) => {
                    const selected = profile.subjects?.find(
                      (s) => String(s.id) === e.target.value
                    );
                    setBooking((prev) => ({
                      ...prev,
                      subjectId: e.target.value,
                      subject: selected?.name || prev.subject
                    }));
                  }}
                  disabled={!profile.subjects?.length}
                >
                  {!profile.subjects?.length && (
                    <option value="">No subjects listed</option>
                  )}
                  {profile.subjects?.map((subject) => (
                    <option key={subject.id} value={subject.id}>
                      {subject.name}
                    </option>
                  ))}
                </select>
              </label>

              <div className="field">
                <span>Lesson mode</span>
                <div className="radio-row">
                  <label className="radio">
                    <input
                      type="radio"
                      name="mode"
                      value="online"
                      checked={booking.mode === "online"}
                      onChange={handleBookingChange}
                    />
                    <span>Online</span>
                  </label>
                  <label className="radio">
                    <input
                      type="radio"
                      name="mode"
                      value="in-person"
                      checked={booking.mode === "in-person"}
                      onChange={handleBookingChange}
                    />
                    <span>In-Person</span>
                  </label>
                </div>
              </div>

              <label className="field">
                <span>Proposed monthly rate (AZN)</span>
                <input
                  name="proposedPrice"
                  type="number"
                  min={0}
                  value={booking.proposedPrice}
                  onChange={handleBookingChange}
                />
              </label>

              <label className="field">
                <span>Note (optional)</span>
                <textarea
                  name="note"
                  rows="3"
                  placeholder="Add a short message for the tutor"
                  value={booking.note}
                  onChange={handleBookingChange}
                />
              </label>

              <button
                type="submit"
                className="btn btn--primary"
                disabled={!isLearner || !availabilitySlots.length}
              >
                {isLearner ? "Send Request" : "Log in as a learner to book"}
              </button>
              {bookingResult && <p className="hint">{bookingResult}</p>}
            </form>
          </div>
        </aside>
      </section>
    </div>
  );
};

export default TutorProfile;
