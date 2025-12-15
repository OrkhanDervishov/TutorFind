package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.CityRepo;
import com.team13.TutorFind.Database.Repos.DistrictRepo;
import com.team13.TutorFind.entity.City;
import com.team13.TutorFind.entity.District;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    
    private final CityRepo cityRepo;
    private final DistrictRepo districtRepo;
    
    public LocationService(CityRepo cityRepo, DistrictRepo districtRepo) {
        this.cityRepo = cityRepo;
        this.districtRepo = districtRepo;
    }
    
    public List<City> getAllCities() {
        return cityRepo.findAll();
    }
    
    public List<District> getAllDistricts() {
        return districtRepo.findAll();
    }
    
    public List<District> getDistrictsByCity(Long cityId) {
        return districtRepo.findByCityId(cityId);
    }
    
    public City getCityById(Long id) {
        return cityRepo.findById(id).orElse(null);
    }
}