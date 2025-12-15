import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="site-footer">
      <div className="site-footer__inner">
        <div className="footer-brand">
          <span className="brand__mark">TF</span>
          <span className="brand__name">TutorFind</span>
        </div>
        <p className="footer-copy">
          Fast, local tutor matching for every learner and educator.
        </p>
        <div className="footer-links">
          <Link to="/search">Find Tutors</Link>
          <Link to="/login">Log In</Link>
          <Link to="/signup">Sign Up</Link>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
