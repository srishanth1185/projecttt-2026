package com.smartpark.controller;

import com.smartpark.dto.StatsDTO;
import com.smartpark.dto.ActivityDTO;
import com.smartpark.service.ParkingService;
import com.smartpark.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ParkingService parkingService;
    private final ActivityService activityService;

    @GetMapping("/global")
    public ResponseEntity<StatsDTO> getGlobalStats() {
        return ResponseEntity.ok(parkingService.getGlobalStats());
    }

    @GetMapping("/locations")
    public ResponseEntity<Map<String, StatsDTO>> getAllLocationStats() {
        Map<String, StatsDTO> stats = new LinkedHashMap<>();
        parkingService.getAllLocations().forEach(loc -> {
            stats.put(loc.getCode(), parkingService.getLocationStats(loc.getCode()));
        });
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityDTO>> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(activityService.getRecentActivity(limit));
    }
}
