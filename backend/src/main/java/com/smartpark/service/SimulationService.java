package com.smartpark.service;

import com.smartpark.model.*;
import com.smartpark.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final ParkingSlotRepository slotRepository;
    private final ActivityLogRepository activityLogRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void simulateParkingActivity() {
        // Clean expired reservations
        bookingService.cleanExpiredReservations();

        // Randomly toggle 1-2 slots
        List<ParkingSlot> allSlots = slotRepository.findAll();
        if (allSlots.isEmpty()) return;

        int count = ThreadLocalRandom.current().nextInt(1, 3);
        for (int i = 0; i < count; i++) {
            ParkingSlot slot = allSlots.get(ThreadLocalRandom.current().nextInt(allSlots.size()));

            // Don't touch reserved slots
            if (slot.getStatus() == ParkingSlot.SlotStatus.RESERVED) continue;

            if (slot.getStatus() == ParkingSlot.SlotStatus.AVAILABLE && Math.random() < 0.6) {
                String plate = generatePlate();
                slot.setStatus(ParkingSlot.SlotStatus.OCCUPIED);
                slot.setVehicleNo(plate);
                slotRepository.save(slot);

                activityLogRepository.save(ActivityLog.builder()
                        .type("occupied")
                        .message("Vehicle " + plate + " parked at " + slot.getSlotLabel())
                        .timestamp(LocalDateTime.now())
                        .build());

            } else if (slot.getStatus() == ParkingSlot.SlotStatus.OCCUPIED && Math.random() < 0.4) {
                String oldPlate = slot.getVehicleNo();
                slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
                slot.setVehicleNo(null);
                slotRepository.save(slot);

                activityLogRepository.save(ActivityLog.builder()
                        .type("released")
                        .message("Vehicle " + (oldPlate != null ? oldPlate : "unknown") + " left from " + slot.getSlotLabel())
                        .timestamp(LocalDateTime.now())
                        .build());
            }
        }
    }

    private String generatePlate() {
        String[] states = {"KA", "MH", "DL", "TN", "AP", "TS", "UP", "GJ", "RJ", "WB"};
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String st = states[r.nextInt(states.length)];
        String d1 = String.format("%02d", r.nextInt(1, 100));
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String l = "" + letters.charAt(r.nextInt(26)) + letters.charAt(r.nextInt(26));
        int d2 = r.nextInt(1000, 10000);
        return st + "-" + d1 + "-" + l + "-" + d2;
    }
}
