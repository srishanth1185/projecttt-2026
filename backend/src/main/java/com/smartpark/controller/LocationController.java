package com.smartpark.controller;

import com.smartpark.dto.LocationDTO;
import com.smartpark.dto.StatsDTO;
import com.smartpark.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final ParkingService parkingService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(parkingService.getAllLocations());
    }

    @GetMapping("/{code}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable String code) {
        return ResponseEntity.ok(parkingService.getLocationByCode(code));
    }

    @GetMapping("/{code}/stats")
    public ResponseEntity<StatsDTO> getLocationStats(@PathVariable String code) {
        return ResponseEntity.ok(parkingService.getLocationStats(code));
    }
}
