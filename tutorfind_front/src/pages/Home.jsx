import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Home = () => {
  const { user } = useAuth();

  return (
    <div className="home">
      <section className="hero">
        <div className="hero__content">
          <span className="pill pill--glow">TutorFind</span>
          <h1>Find a local tutor fast.</h1>
          <p className="lede">
            Search by city, district, subject, and price. Simple, direct, and
            ready to book.
          </p>

          <div className={`hero__actions ${user ? "hero__actions--single" : ""}`}>
            <Link
              className={`btn btn--primary ${user ? "btn--wide" : ""}`}
              to="/search"
            >
              Search tutors
            </Link>
            {!user && (
              <Link className="btn btn--ghost" to="/signup">
                Tutor or learner? Sign up
              </Link>
            )}
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
