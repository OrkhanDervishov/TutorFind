package com.team13.TutorFind.dto;

public class DistrictDTO {
    private Long id;
    private String name;
    private Long cityId;
    private String cityName;
    
    public DistrictDTO() {}
    
    public DistrictDTO(Long id, String name, Long cityId, String cityName) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.cityName = cityName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
}