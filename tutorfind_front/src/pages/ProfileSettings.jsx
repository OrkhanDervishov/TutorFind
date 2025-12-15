import { useEffect, useMemo, useState } from "react";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";

const ProfileSettings = () => {
  const { user, token } = useAuth();
  const role = (user?.role || "").toUpperCase();
  const [profile, setProfile] = useState(null);
  const [subjects, setSubjects] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState("");
  const [selectedDistrictId, setSelectedDistrictId] = useState("");
  const [status, setStatus] = useState("");
  const [tutorProfileId, setTutorProfileId] = useState(null);
  const [editProfile, setEditProfile] = useState({
    headline: "",
    bio: "",
    qualifications: "",
    experienceYears: "",
    monthlyRate: ""
  });

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const [subjectList, districtList] = await Promise.all([
          api.subjects(),
          api.districts()
        ]);
        setSubjects(subjectList);
        setDistricts(districtList);
      } catch (err) {
        setStatus(err.message || "Failed to load reference data");
      }
    };
    bootstrap();
  }, []);

  useEffect(() => {
    if (role !== "TUTOR") return;
    const resolve = async () => {
      try {
        const tutors = await api.searchTutors({});
        const mine = tutors.find((t) => t.userId === user?.id);
        if (mine?.id) {
          setTutorProfileId(mine.id);
          const prof = await api.tutorProfile(mine.id);
          setProfile(prof);
          setEditProfile({
            headline: prof.headline || "",
            bio: prof.bio || "",
            qualifications: prof.qualifications || "",
            experienceYears: prof.experienceYears || "",
            monthlyRate: prof.monthlyRate || ""
          });
        } else {
          setStatus(
            "No tutor profile found for this account. Create one in the backend first."
          );
        }
      } catch (err) {
        setStatus(err.message || "Failed to load profile");
      }
    };
    resolve();
  }, [role, user]);

  const handleAddSubject = async () => {
    if (!selectedSubjectId) return;
    if (!token) {
      setStatus("Log in to update subjects.");
      return;
    }
    setStatus("");
    try {
      await api.addSubject(token, Number(selectedSubjectId));
      if (tutorProfileId) {
        const prof = await api.tutorProfile(tutorProfileId);
        setProfile(prof);
      }
      setStatus("Subject added to your profile.");
    } catch (err) {
      setStatus(err.message || "Unable to add subject");
    }
  };

  const handleRemoveSubject = async (subjectId) => {
    if (!token) {
      setStatus("Log in to update subjects.");
      return;
    }
    setStatus("");
    try {
      await api.removeSubject(token, subjectId);
      if (tutorProfileId) {
        const prof = await api.tutorProfile(tutorProfileId);
        setProfile(prof);
      }
      setStatus("Subject removed.");
    } catch (err) {
      setStatus(err.message || "Unable to remove subject");
    }
  };

  const handleAddDistrict = async () => {
    if (!selectedDistrictId) return;
    if (!token) {
      setStatus("Log in to update districts.");
      return;
    }
    setStatus("");
    try {
      await api.addDistrict(token, Number(selectedDistrictId));
      if (tutorProfileId) {
        const prof = await api.tutorProfile(tutorProfileId);
        setProfile(prof);
      }
      setStatus("District added.");
    } catch (err) {
      setStatus(err.message || "Unable to add district");
    }
  };

  const handleRemoveDistrict = async (name) => {
    const district = districts.find((d) => d.name === name);
    if (!district) {
      setStatus("District id not found for removal.");
      return;
    }
    if (!token) {
      setStatus("Log in to update districts.");
      return;
    }
    setStatus("");
    try {
      await api.removeDistrict(token, district.id);
      if (tutorProfileId) {
        const prof = await api.tutorProfile(tutorProfileId);
        setProfile(prof);
      }
      setStatus("District removed.");
    } catch (err) {
      setStatus(err.message || "Unable to remove district");
    }
  };

  const districtOptionsForCity = useMemo(() => {
    if (!profile?.city) return districts;
    return districts.filter((d) => d.cityName === profile.city);
  }, [districts, profile]);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    if (!token) {
      setStatus("Log in to update profile.");
      return;
    }
    setStatus("");
    try {
      await api.updateTutorProfile(token, {
        headline: editProfile.headline || null,
        bio: editProfile.bio || null,
        qualifications: editProfile.qualifications || null,
        experienceYears: editProfile.experienceYears
          ? Number(editProfile.experienceYears)
          : null,
        monthlyRate: editProfile.monthlyRate
          ? Number(editProfile.monthlyRate)
          : null
      });
      if (tutorProfileId) {
        const prof = await api.tutorProfile(tutorProfileId);
        setProfile(prof);
        setEditProfile({
          headline: prof.headline || "",
          bio: prof.bio || "",
          qualifications: prof.qualifications || "",
          experienceYears: prof.experienceYears || "",
          monthlyRate: prof.monthlyRate || ""
        });
      }
      setStatus("Profile updated.");
    } catch (err) {
      setStatus(err.message || "Failed to update profile");
    }
  };

  if (!user) {
    return (
      <div className="page-card">
        <p className="hint">Log in to manage your profile.</p>
      </div>
    );
  }

  return (
    <div className="page-narrow">
      <div className="card">
        <h2>Profile Settings</h2>
        <p className="hint">
          Manage your profile, subjects, and service districts.
        </p>
        {status && <p className="hint">{status}</p>}

        <div className="stacked-form">
          <div className="two-col">
            <label className="field">
              <span>First Name</span>
              <input value={user.firstName || ""} readOnly />
            </label>
            <label className="field">
              <span>Last Name</span>
              <input value={user.lastName || ""} readOnly />
            </label>
          </div>
          <label className="field">
            <span>Email</span>
            <input value={user.email || ""} readOnly />
          </label>
          <label className="field">
            <span>Role</span>
            <input value={role} readOnly />
          </label>
        </div>
      </div>

      {role === "TUTOR" && profile && (
        <div className="card mt-4">
          <h3>Tutor Profile</h3>
          <form className="stacked-form" onSubmit={handleUpdateProfile}>
            <label className="field">
              <span>Headline</span>
              <input
                value={editProfile.headline}
                onChange={(e) =>
                  setEditProfile((prev) => ({ ...prev, headline: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>Bio</span>
              <textarea
                rows="3"
                value={editProfile.bio}
                onChange={(e) =>
                  setEditProfile((prev) => ({ ...prev, bio: e.target.value }))
                }
              />
            </label>
            <label className="field">
              <span>Qualifications</span>
              <textarea
                rows="3"
                value={editProfile.qualifications}
                onChange={(e) =>
                  setEditProfile((prev) => ({
                    ...prev,
                    qualifications: e.target.value
                  }))
                }
              />
            </label>
            <div className="two-col">
              <label className="field">
                <span>Experience (years)</span>
                <input
                  type="number"
                  min="0"
                  value={editProfile.experienceYears}
                  onChange={(e) =>
                    setEditProfile((prev) => ({
                      ...prev,
                      experienceYears: e.target.value
                    }))
                  }
                />
              </label>
              <label className="field">
                <span>Monthly Rate (AZN)</span>
                <input
                  type="number"
                  min="0"
                  value={editProfile.monthlyRate}
                  onChange={(e) =>
                    setEditProfile((prev) => ({
                      ...prev,
                      monthlyRate: e.target.value
                    }))
                  }
                />
              </label>
            </div>
            <button type="submit" className="btn btn--primary">
              Save Profile
            </button>
          </form>

          <div className="stacked-form">
            <div className="two-col">
              <label className="field">
                <span>Add Subject</span>
                <select
                  value={selectedSubjectId}
                  onChange={(e) => setSelectedSubjectId(e.target.value)}
                >
                  <option value="">Select a subject</option>
                  {subjects.map((subj) => (
                    <option key={subj.id} value={subj.id}>
                      {subj.name} ({subj.category})
                    </option>
                  ))}
                </select>
              </label>
              <div className="field">
                <span>&nbsp;</span>
                <button
                  type="button"
                  className="btn btn--primary"
                  onClick={handleAddSubject}
                  disabled={!selectedSubjectId}
                >
                  Add Subject
                </button>
              </div>
            </div>

            <div className="tags">
              {profile.subjects?.length ? (
                profile.subjects.map((s) => (
                  <button
                    key={s.id}
                    type="button"
                    className="chip"
                    onClick={() => handleRemoveSubject(s.id)}
                    title="Remove subject"
                  >
                    {s.name}
                  </button>
                ))
              ) : (
                <p className="hint">No subjects listed yet.</p>
              )}
            </div>

            <div className="two-col">
              <label className="field">
                <span>Add District</span>
                <select
                  value={selectedDistrictId}
                  onChange={(e) => setSelectedDistrictId(e.target.value)}
                >
                  <option value="">Select a district</option>
                  {districtOptionsForCity.map((district) => (
                    <option key={district.id} value={district.id}>
                      {district.name}
                    </option>
                  ))}
                </select>
              </label>
              <div className="field">
                <span>&nbsp;</span>
                <button
                  type="button"
                  className="btn btn--primary"
                  onClick={handleAddDistrict}
                  disabled={!selectedDistrictId}
                >
                  Add District
                </button>
              </div>
            </div>

            <div className="tags">
              {profile.districts?.length ? (
                profile.districts.map((name) => (
                  <button
                    key={name}
                    type="button"
                    className="chip"
                    onClick={() => handleRemoveDistrict(name)}
                    title="Remove district"
                  >
                    {name}
                  </button>
                ))
              ) : (
                <p className="hint">No districts listed yet.</p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileSettings;
