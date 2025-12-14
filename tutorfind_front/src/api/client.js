const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const toQueryString = (params = {}) => {
  const entries = Object.entries(params).filter(
    ([, value]) => value !== undefined && value !== null && value !== ""
  );
  if (!entries.length) return "";
  const search = new URLSearchParams();
  entries.forEach(([key, value]) => {
    search.append(key, value);
  });
  return `?${search.toString()}`;
};

const request = async (path, { method = "GET", body, token, headers } = {}) => {
  const init = {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers
    }
  };

  if (body !== undefined) {
    init.body = JSON.stringify(body);
  }

  const res = await fetch(`${API_URL}${path}`, init);
  const contentType = res.headers.get("content-type");
  const isJson = contentType && contentType.includes("application/json");
  const payload = isJson ? await res.json() : await res.text();

  if (!res.ok) {
    const message =
      typeof payload === "string"
        ? payload
        : payload?.message || "Request failed";
    throw new Error(message);
  }

  return payload;
};

export const api = {
  API_URL,
  login: (email, password) =>
    request("/login", { method: "POST", body: { email, password } }),
  register: (data) => request("/register", { method: "POST", body: data }),
  searchTutors: (params) => request(`/api/tutors${toQueryString(params)}`),
  tutorProfile: (id) => request(`/api/tutors/${id}`),
  tutorReviews: (tutorId) => request(`/api/tutors/${tutorId}/reviews`),
  cities: () => request("/api/locations/cities"),
  districts: () => request("/api/locations/districts"),
  districtsByCity: (cityId) =>
    request(`/api/locations/cities/${cityId}/districts`),
  subjects: () => request("/api/subjects"),
  subjectsByCategory: () => request("/api/subjects/categories"),
  createBooking: (token, payload) =>
    request("/api/bookings", { method: "POST", token, body: payload }),
  bookingsSent: (token, status) =>
    request(
      `/api/bookings/sent${toQueryString({ status })}`,
      { token }
    ),
  bookingsReceived: (token, status) =>
    request(
      `/api/bookings/received${toQueryString({ status })}`,
      { token }
    ),
  acceptBooking: (token, id, response) =>
    request(`/api/bookings/${id}/accept`, {
      method: "PUT",
      token,
      body: response ? { response } : undefined
    }),
  declineBooking: (token, id, response) =>
    request(`/api/bookings/${id}/decline`, {
      method: "PUT",
      token,
      body: response ? { response } : undefined
    }),
  createReview: (token, payload) =>
    request("/api/reviews", { method: "POST", token, body: payload }),
  myReviews: (token) => request("/api/reviews/my-reviews", { token }),
  feedbackReceived: (token) =>
    request("/api/feedback/received", { token }),
  feedbackGiven: (token) =>
    request("/api/feedback/given", { token }),
  feedbackById: (token, id) =>
    request(`/api/feedback/${id}`, { token }),
  createFeedback: (token, payload) =>
    request("/api/feedback", { method: "POST", token, body: payload }),
  addAvailability: (token, payload) =>
    request("/api/tutors/availability", { method: "POST", token, body: payload }),
  removeAvailability: (token, slotId) =>
    request(`/api/tutors/availability/${slotId}`, {
      method: "DELETE",
      token
    }),
  addSubject: (token, subjectId) =>
    request("/api/tutors/subjects", {
      method: "POST",
      token,
      body: { subjectId }
    }),
  removeSubject: (token, subjectId) =>
    request(`/api/tutors/subjects/${subjectId}`, { method: "DELETE", token }),
  addDistrict: (token, districtId) =>
    request("/api/tutors/districts", {
      method: "POST",
      token,
      body: { districtId }
    }),
  removeDistrict: (token, districtId) =>
    request(`/api/tutors/districts/${districtId}`, {
      method: "DELETE",
      token
    }),
  myAvailability: (token) =>
    request("/api/tutors/availability", { token }),
  updateTutorProfile: (token, payload) =>
    request("/api/tutors/profile", { method: "PUT", token, body: payload }),

  // Classes & enrollments
  listClasses: (status) =>
    request(`/api/classes${toQueryString({ status })}`),
  classById: (id) => request(`/api/classes/${id}`),
  createClass: (token, payload) =>
    request("/api/classes", { method: "POST", token, body: payload }),
  updateClass: (token, id, payload) =>
    request(`/api/classes/${id}`, { method: "PUT", token, body: payload }),
  deleteClass: (token, id) =>
    request(`/api/classes/${id}`, { method: "DELETE", token }),
  myClasses: (token) => request("/api/classes/my-classes", { token }),
  classRoster: (token, id) =>
    request(`/api/classes/${id}/roster`, { token }),
  enrollInClass: (token, id) =>
    request(`/api/classes/${id}/enroll`, { method: "POST", token }),
  myEnrollments: (token, status) =>
    request(
      `/api/classes/enrollments/my${toQueryString({ status })}`,
      { token }
    ),
  dropEnrollment: (token, enrollmentId) =>
    request(`/api/classes/enrollments/${enrollmentId}`, {
      method: "DELETE",
      token
    }),

  // Admin
  adminStats: (token) => request("/api/admin/stats", { token }),
  adminUsers: (token) => request("/api/admin/users", { token }),
  adminUnverifiedTutors: (token) =>
    request("/api/admin/tutors/unverified", { token }),
  adminVerifyTutor: (token, tutorId) =>
    request(`/api/admin/tutors/${tutorId}/verify`, {
      method: "PUT",
      token
    }),
  adminUnverifyTutor: (token, tutorId) =>
    request(`/api/admin/tutors/${tutorId}/unverify`, {
      method: "PUT",
      token
    }),
  adminPendingReviews: (token) =>
    request("/api/admin/reviews/pending", { token }),
  adminApproveReview: (token, reviewId) =>
    request(`/api/admin/reviews/${reviewId}/approve`, {
      method: "PUT",
      token
    }),
  adminRejectReview: (token, reviewId, reason) =>
    request(`/api/admin/reviews/${reviewId}/reject`, {
      method: "PUT",
      token,
      body: reason ? { reason } : undefined
    }),
  adminDeactivateUser: (token, userId) =>
    request(`/api/admin/users/${userId}/deactivate`, {
      method: "PUT",
      token
    }),
  adminActivateUser: (token, userId) =>
    request(`/api/admin/users/${userId}/activate`, {
      method: "PUT",
      token
    })
};

export default api;
