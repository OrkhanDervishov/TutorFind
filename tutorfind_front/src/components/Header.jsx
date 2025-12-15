import { Link, NavLink } from "react-router-dom";
import { useState } from "react";
import { useAuth } from "../context/AuthContext";

const Header = () => {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);

  const toggleMenu = () => setOpen((prev) => !prev);
  const closeMenu = () => setOpen(false);

  const role = (user?.role || "").toUpperCase();
  const isTutor = role === "TUTOR";
  const isLearner = role === "LEARNER";
  const isAdmin = role === "ADMIN";
  const displayName =
    [user?.firstName, user?.lastName].filter(Boolean).join(" ") ||
    user?.email ||
    "User";
  const initial =
    (user?.firstName?.[0] || user?.lastName?.[0] || displayName?.[0] || "U")
      .toUpperCase();

  return (
    <header className="site-header">
      <div className="site-header__inner">
        <Link to="/" className="brand">
          <span className="brand__mark">TF</span>
          <span className="brand__name">TutorFind</span>
        </Link>

        <nav className="nav-links" aria-label="Main navigation">
          <NavLink to="/" end>
            Home
          </NavLink>
          <NavLink to="/search">Search Tutors</NavLink>
          <NavLink to="/classes">Classes</NavLink>
          {isAdmin && <NavLink to="/admin">Admin</NavLink>}
        </nav>

        <div className="auth-actions">
          {user ? (
            <div className="user-menu">
              <button
                className="user-trigger"
                onClick={toggleMenu}
                aria-expanded={open}
              >
                <span className="avatar">
                  {initial}
                </span>
                <span className="user-name">{displayName}</span>
              </button>
              {open && (
                <div className="dropdown" role="menu">
                  {isTutor ? (
                    <>
                      <Link to="/dashboard" onClick={closeMenu}>
                        Bookings
                      </Link>
                      <Link to="/tutor/classes" onClick={closeMenu}>
                        My Classes
                      </Link>
                      <Link to="/settings/profile" onClick={closeMenu}>
                        Profile Settings
                      </Link>
                      <Link to="/settings/availability" onClick={closeMenu}>
                        My Availability
                      </Link>
                    </>
                  ) : (
                    <>
                      <Link to="/enrollments" onClick={closeMenu}>
                        My Enrollments
                      </Link>
                      <Link to="/learner" onClick={closeMenu}>
                        My Sessions
                      </Link>
                      <Link to="/settings/profile" onClick={closeMenu}>
                        Profile Settings
                      </Link>
                    </>
                  )}
                  {isAdmin && (
                    <Link to="/admin" onClick={closeMenu}>
                      Admin
                    </Link>
                  )}
                  <button type="button" onClick={logout}>
                    Log Out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <>
              <Link className="btn btn--ghost" to="/login">
                Log In
              </Link>
              <Link className="btn btn--primary" to="/signup">
                Sign Up
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
