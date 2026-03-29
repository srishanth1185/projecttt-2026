package com.smartpark.controller;

import com.smartpark.dto.SlotDTO;
import com.smartpark.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final ParkingService parkingService;

    @GetMapping("/{locationCode}/{floorCode}/{zoneCode}")
    public ResponseEntity<List<SlotDTO>> getSlotsForZone(
            @PathVariable String locationCode,
            @PathVariable String floorCode,
            @PathVariable String zoneCode) {
        return ResponseEntity.ok(parkingService.getSlotsForZone(locationCode, floorCode, zoneCode));
    }

    @PostMapping("/{slotId}/toggle")
    public ResponseEntity<SlotDTO> toggleSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(parkingService.toggleSlotStatus(slotId));
    }
}
