const Search = () => {
  return (
    <div className="page-card">
      <header className="page-card__header">
        <div>
          <p className="eyebrow">Tutor Discovery</p>
          <h1>Search for tutors</h1>
        </div>
        <p className="hint">
          Filter by city, district, subject, hourly price, and availability.
        </p>
      </header>
      <form className="filter-grid">
        <label className="field">
          <span>City</span>
          <input name="city" placeholder="Select a city" />
        </label>
        <label className="field">
          <span>District</span>
          <input name="district" placeholder="Select a district" />
        </label>
        <label className="field">
          <span>Subject</span>
          <input name="subject" placeholder="e.g., Math, English" />
        </label>
        <label className="field">
          <span>Price (per hour)</span>
          <input name="price" placeholder="$40" />
        </label>
        <label className="field">
          <span>Availability</span>
          <select defaultValue="">
            <option value="" disabled>
              Choose a slot
            </option>
            <option value="morning">Morning</option>
            <option value="afternoon">Afternoon</option>
            <option value="evening">Evening</option>
          </select>
        </label>
        <button type="submit" className="btn btn--primary">
          Apply Filters
        </button>
      </form>
      <div className="results-placeholder">
        <p>Results will appear here once data wiring is added.</p>
      </div>
    </div>
  );
};

export default Search;
