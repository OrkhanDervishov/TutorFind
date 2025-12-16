package com.team13.TutorFind.dto;

import java.math.BigDecimal;

public class SearchRequest {
    private String city;
    private String district;
    private String subject;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Float minRating;
    private String availabilityDay;
    private String availabilityStart;
    private String availabilityEnd;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "rating"; // rating, price_asc, price_desc, experience, name
    
    // Constructors
    public SearchRequest() {}
    
    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    
    public Float getMinRating() { return minRating; }
    public void setMinRating(Float minRating) { this.minRating = minRating; }
    
    public String getAvailabilityDay() { return availabilityDay; }
    public void setAvailabilityDay(String availabilityDay) { this.availabilityDay = availabilityDay; }

    public String getAvailabilityStart() { return availabilityStart; }
    public void setAvailabilityStart(String availabilityStart) { this.availabilityStart = availabilityStart; }

    public String getAvailabilityEnd() { return availabilityEnd; }
    public void setAvailabilityEnd(String availabilityEnd) { this.availabilityEnd = availabilityEnd; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}
