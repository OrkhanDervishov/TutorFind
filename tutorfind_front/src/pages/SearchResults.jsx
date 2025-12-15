import { useEffect, useMemo, useState } from "react";
import TutorCard from "../components/TutorCard";
import api from "../api/client";

const initialFilters = {
  city: "",
  district: "",
  subject: "",
  minPrice: "",
  maxPrice: "",
  minRating: "",
  sortBy: "rating"
};

const sortOptions = [
  { value: "rating", label: "Rating" },
  { value: "price_asc", label: "Price: Low to High" },
  { value: "price_desc", label: "Price: High to Low" }
];

const SearchResults = () => {
  const [filters, setFilters] = useState(initialFilters);
  const [tutors, setTutors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [subjects, setSubjects] = useState([]);
  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const [cityList, districtList, subjectList] = await Promise.all([
          api.cities(),
          api.districts(),
          api.subjects()
        ]);
        setCities(cityList);
        setDistricts(districtList);
        setSubjects(subjectList);
      } catch (err) {
        console.error("Failed to load filter data", err);
      }
    };
    bootstrap();
    fetchTutors(initialFilters);
  }, []);

  const fetchTutors = async (activeFilters = filters) => {
    setLoading(true);
    setError("");
    try {
      const payload = {
        city: activeFilters.city || undefined,
        district: activeFilters.district || undefined,
        subject: activeFilters.subject || undefined,
        minPrice: activeFilters.minPrice || undefined,
        maxPrice: activeFilters.maxPrice || undefined,
        minRating: activeFilters.minRating || undefined,
        sortBy: activeFilters.sortBy || "rating"
      };
      const data = await api.searchTutors(payload);
      setTutors(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Unable to fetch tutors");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetchTutors(filters);
  };

  const availableDistricts = useMemo(() => {
    if (!filters.city) return districts;
    const city = cities.find((c) => c.name === filters.city);
    if (!city) return districts;
    return districts.filter((d) => d.cityId === city.id);
  }, [cities, districts, filters.city]);

  return (
    <div className="search-layout">
      <aside className="filters-panel" aria-label="Filters">
        <form className="filters-panel__inner" onSubmit={handleSubmit}>
          <header className="filters-header">
            <p className="eyebrow">Refine</p>
            <h3>Filters</h3>
          </header>

          <div className="filter-group">
            <label className="field">
              <span>City</span>
              <select
                name="city"
                value={filters.city}
                onChange={handleChange}
              >
                <option value="">Any</option>
                {cities.map((city) => (
                  <option key={city.id} value={city.name}>
                    {city.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>District</span>
              <select
                name="district"
                value={filters.district}
                onChange={handleChange}
                disabled={!availableDistricts.length}
              >
                <option value="">Any</option>
                {availableDistricts.map((district) => (
                  <option key={district.id} value={district.name}>
                    {district.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Subject</span>
              <select
                name="subject"
                value={filters.subject}
                onChange={handleChange}
              >
                <option value="">Any</option>
                {subjects.map((subject) => (
                  <option key={subject.id} value={subject.name}>
                    {subject.name}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <div className="filter-group">
            <div className="price-row">
              <label className="field">
                <span>Min Price (AZN)</span>
                <input
                  name="minPrice"
                  type="number"
                  min={0}
                  value={filters.minPrice}
                  onChange={handleChange}
                  placeholder="0"
                />
              </label>
              <label className="field">
                <span>Max Price (AZN)</span>
                <input
                  name="maxPrice"
                  type="number"
                  min={0}
                  value={filters.maxPrice}
                  onChange={handleChange}
                  placeholder="500"
                />
              </label>
            </div>
            <label className="field">
              <span>Min Rating</span>
              <input
                name="minRating"
                type="number"
                min="0"
                max="5"
                step="0.1"
                placeholder="e.g., 4"
                value={filters.minRating}
                onChange={handleChange}
              />
            </label>
          </div>

          <button type="submit" className="btn btn--primary btn--full">
            Apply Filters
          </button>
        </form>
      </aside>

      <section className="results-panel">
        <header className="results-header">
          <div>
            <p className="eyebrow">Search results</p>
            <h2>
              {loading ? "Loading tutors..." : `${tutors.length} Tutors found`}
            </h2>
            {error && <p className="hint text-red-500">{error}</p>}
          </div>
          <label className="field field--inline">
            <span>Sort by</span>
            <select
              name="sortBy"
              value={filters.sortBy}
              onChange={(e) => {
                const value = e.target.value;
                setFilters((prev) => ({ ...prev, sortBy: value }));
                fetchTutors({ ...filters, sortBy: value });
              }}
            >
              {sortOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </label>
        </header>

        <div className="results-grid">
          {!loading && tutors.map((tutor) => (
            <TutorCard key={tutor.id} tutor={tutor} />
          ))}
          {loading && <p className="hint">Fetching tutors...</p>}
          {!loading && !tutors.length && !error && (
            <p className="hint">No tutors match these filters yet.</p>
          )}
        </div>
      </section>
    </div>
  );
};

export default SearchResults;
