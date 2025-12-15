package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.LocationService;
import com.team13.TutorFind.dto.CityDTO;
import com.team13.TutorFind.dto.DistrictDTO;
import com.team13.TutorFind.entity.City;
import com.team13.TutorFind.entity.District;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/locations")
public class LocationController {
    
    private final LocationService locationService;
    
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @GetMapping("/cities")
    public ResponseEntity<List<CityDTO>> getAllCities() {
        List<City> cities = locationService.getAllCities();
        List<CityDTO> cityDTOs = new ArrayList<>();
        
        for (City city : cities) {
            CityDTO dto = new CityDTO(
                city.getId(),
                city.getName(),
                city.getCountry()
            );
            cityDTOs.add(dto);
        }
        
        return ResponseEntity.ok(cityDTOs);
    }
    
    @GetMapping("/districts")
    public ResponseEntity<List<DistrictDTO>> getAllDistricts() {
        List<District> districts = locationService.getAllDistricts();
        List<DistrictDTO> districtDTOs = new ArrayList<>();
        
        for (District district : districts) {
            DistrictDTO dto = new DistrictDTO(
                district.getId(),
                district.getName(),
                district.getCity().getId(),
                district.getCity().getName()
            );
            districtDTOs.add(dto);
        }
        
        return ResponseEntity.ok(districtDTOs);
    }
    
    @GetMapping("/cities/{cityId}/districts")
    public ResponseEntity<List<DistrictDTO>> getDistrictsByCity(@PathVariable Long cityId) {
        List<District> districts = locationService.getDistrictsByCity(cityId);
        List<DistrictDTO> districtDTOs = new ArrayList<>();
        
        City city = locationService.getCityById(cityId);
        String cityName = city != null ? city.getName() : null;
        
        for (District district : districts) {
            DistrictDTO dto = new DistrictDTO(
                district.getId(),
                district.getName(),
                cityId,
                cityName
            );
            districtDTOs.add(dto);
        }
        
        return ResponseEntity.ok(districtDTOs);
    }
}