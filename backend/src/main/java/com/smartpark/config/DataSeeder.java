package com.smartpark.config;

import com.smartpark.model.*;
import com.smartpark.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (locationRepository.count() > 0) {
            log.info("Data already seeded, skipping.");
            return;
        }

        log.info("Seeding parking data...");

        // ─── Mall ───
        Location mall = createLocation("mall", "City Center Mall", "🏬",
                "Downtown, Main Street", "hsla(210, 100%, 56%, 0.12)");

        Floor mallB1 = createFloor("B1", "Basement 1", mall);
        createZone("A", "Zone A", 3, 8, mallB1, List.of(0, 1));
        createZone("B", "Zone B", 3, 8, mallB1, List.of());

        Floor mallB2 = createFloor("B2", "Basement 2", mall);
        createZone("A", "Zone A", 3, 10, mallB2, List.of(0));
        createZone("B", "Zone B", 2, 10, mallB2, List.of());

        Floor mallG = createFloor("G", "Ground Floor", mall);
        createZone("A", "Zone A", 2, 6, mallG, List.of(0, 1));

        locationRepository.save(mall);

        // ─── Hospital ───
        Location hospital = createLocation("hospital", "General Hospital", "🏥",
                "Health Avenue, Medical District", "hsla(152, 69%, 45%, 0.12)");

        Floor hospG = createFloor("G", "Ground Floor", hospital);
        createZone("EM", "Emergency", 2, 6, hospG, List.of(0, 1, 2));
        createZone("VS", "Visitor", 3, 8, hospG, List.of(0));

        Floor hospB1 = createFloor("B1", "Basement", hospital);
        createZone("ST", "Staff", 3, 10, hospB1, List.of());
        createZone("VS", "Visitor", 2, 8, hospB1, List.of(0));

        locationRepository.save(hospital);

        // ─── Office ───
        Location office = createLocation("office", "Tech Park Tower", "🏢",
                "Innovation Blvd, Business Park", "hsla(260, 70%, 60%, 0.12)");

        Floor offB1 = createFloor("B1", "Basement 1", office);
        createZone("A", "Zone A", 4, 10, offB1, List.of(0, 1));
        createZone("B", "Zone B", 3, 10, offB1, List.of());

        Floor offB2 = createFloor("B2", "Basement 2", office);
        createZone("A", "Zone A", 4, 10, offB2, List.of(0));

        locationRepository.save(office);

        // ─── Campus ───
        Location campus = createLocation("campus", "University Campus", "🎓",
                "College Road, Academic Zone", "hsla(40, 95%, 55%, 0.12)");

        Floor campL1 = createFloor("L1", "Lot 1 - Main", campus);
        createZone("STU", "Student", 4, 10, campL1, List.of(0));
        createZone("FAC", "Faculty", 2, 8, campL1, List.of(0, 1));

        Floor campL2 = createFloor("L2", "Lot 2 - Library", campus);
        createZone("VS", "Visitor", 3, 8, campL2, List.of(0, 1));

        locationRepository.save(campus);

        // Seed initial activity
        LocalDateTime now = LocalDateTime.now();
        activityLogRepository.saveAll(List.of(
            ActivityLog.builder().type("occupied").message("Vehicle KA-01-AB-1234 parked at Mall B1-A1-3").timestamp(now.minusMinutes(2)).build(),
            ActivityLog.builder().type("released").message("Slot B2-A1-5 freed at Tech Park").timestamp(now.minusMinutes(5)).build(),
            ActivityLog.builder().type("booked").message("Slot G-EM1-2 reserved at Hospital").timestamp(now.minusMinutes(10)).build(),
            ActivityLog.builder().type("occupied").message("Vehicle MH-12-CD-5678 parked at Campus Lot 1").timestamp(now.minusMinutes(15)).build(),
            ActivityLog.builder().type("released").message("Slot B1-B2-7 freed at Mall").timestamp(now.minusMinutes(25)).build()
        ));

        log.info("Data seeding complete!");
    }

    private Location createLocation(String code, String name, String icon, String address, String color) {
        return Location.builder()
                .code(code).name(name).icon(icon).address(address).color(color)
                .build();
    }

    private Floor createFloor(String code, String name, Location location) {
        Floor floor = Floor.builder().code(code).name(name).location(location).build();
        location.getFloors().add(floor);
        return floor;
    }

    private void createZone(String code, String name, int rows, int slotsPerRow, Floor floor, List<Integer> handicapped) {
        Zone zone = Zone.builder()
                .code(code).name(name).rows(rows).slotsPerRow(slotsPerRow).floor(floor)
                .build();
        floor.getZones().add(zone);

        int total = rows * slotsPerRow;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int i = 0; i < total; i++) {
            boolean isHandicapped = handicapped.contains(i);
            double r = Math.random();
            ParkingSlot.SlotStatus status;
            String vehicleNo = null;

            if (r < 0.55) {
                status = ParkingSlot.SlotStatus.OCCUPIED;
                vehicleNo = generatePlate();
            } else if (r < 0.65) {
                status = ParkingSlot.SlotStatus.RESERVED;
                vehicleNo = generatePlate();
            } else {
                status = ParkingSlot.SlotStatus.AVAILABLE;
            }

            String label = code + (i / slotsPerRow + 1) + "-" + (i % slotsPerRow + 1);

            ParkingSlot slot = ParkingSlot.builder()
                    .slotLabel(label)
                    .slotIndex(i)
                    .status(status)
                    .type(isHandicapped ? ParkingSlot.SlotType.HANDICAPPED : ParkingSlot.SlotType.CAR)
                    .vehicleNo(vehicleNo)
                    .zone(zone)
                    .build();

            zone.getSlots().add(slot);
        }
    }

    private String generatePlate() {
        String[] states = {"KA", "MH", "DL", "TN", "AP", "TS", "UP", "GJ", "RJ", "WB"};
        ThreadLocalRandom r = ThreadLocalRandom.current();
        return states[r.nextInt(states.length)] + "-" +
               String.format("%02d", r.nextInt(1, 100)) + "-" +
               (char) ('A' + r.nextInt(26)) + (char) ('A' + r.nextInt(26)) + "-" +
               r.nextInt(1000, 10000);
    }
}
