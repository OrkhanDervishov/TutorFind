import { Link } from "react-router-dom";

const TutorCard = ({ tutor }) => {
  const fullName =
    [tutor.firstName, tutor.lastName].filter(Boolean).join(" ") ||
    tutor.name ||
    "Tutor";
  const rating = typeof tutor.rating === "number" ? tutor.rating : null;
  const reviewCount = tutor.reviewCount ?? tutor.reviews ?? 0;
  const price =
    tutor.monthlyRate !== undefined && tutor.monthlyRate !== null
      ? `${tutor.monthlyRate} AZN/month`
      : "N/A";

  return (
    <article className="tutor-card">
      <div className="tutor-card__header">
        <div>
          <h3>{fullName}</h3>
          {tutor.city && <p className="hint">{tutor.city}</p>}
          {tutor.headline && (
            <p className="tutor-card__subjects">{tutor.headline}</p>
          )}
        </div>
      </div>
      <div className="tutor-card__meta">
        <span className="rating">
          {rating !== null ? (
            <>
              Rating {rating.toFixed(1)}{" "}
              <span className="hint">({reviewCount} reviews)</span>
            </>
          ) : (
            <span className="hint">No rating yet</span>
          )}
        </span>
        <span className="price">{price}</span>
      </div>
      <Link className="btn btn--ghost btn--full" to={`/tutor/${tutor.id}`}>
        View Profile
      </Link>
    </article>
  );
};

export default TutorCard;
