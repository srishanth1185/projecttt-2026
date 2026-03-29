package com.smartpark.service;

import com.smartpark.dto.LocationDTO;
import com.smartpark.dto.SlotDTO;
import com.smartpark.dto.StatsDTO;
import com.smartpark.model.*;
import com.smartpark.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final LocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO getLocationByCode(String code) {
        Location loc = locationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Location not found: " + code));
        return toLocationDTO(loc);
    }

    public List<SlotDTO> getSlotsForZone(String locationCode, String floorCode, String zoneCode) {
        List<ParkingSlot> slots = slotRepository.findByLocationAndFloorAndZone(locationCode, floorCode, zoneCode);
        if (slots.isEmpty()) return List.of();

        int slotsPerRow = slots.get(0).getZone().getSlotsPerRow();
        return slots.stream()
                .map(s -> toSlotDTO(s, slotsPerRow))
                .collect(Collectors.toList());
    }

    public StatsDTO getGlobalStats() {
        long total = slotRepository.count();
        long available = slotRepository.countByStatus(ParkingSlot.SlotStatus.AVAILABLE);
        long occupied = slotRepository.countByStatus(ParkingSlot.SlotStatus.OCCUPIED);
        long reserved = slotRepository.countByStatus(ParkingSlot.SlotStatus.RESERVED);

        return StatsDTO.builder()
                .total(total)
                .available(available)
                .occupied(occupied)
                .reserved(reserved)
                .build();
    }

    public StatsDTO getLocationStats(String locationCode) {
        long total = slotRepository.countByLocationCode(locationCode);
        long available = slotRepository.countByLocationCodeAndStatus(locationCode, ParkingSlot.SlotStatus.AVAILABLE);
        long occupied = slotRepository.countByLocationCodeAndStatus(locationCode, ParkingSlot.SlotStatus.OCCUPIED);
        long reserved = slotRepository.countByLocationCodeAndStatus(locationCode, ParkingSlot.SlotStatus.RESERVED);

        return StatsDTO.builder()
                .total(total)
                .available(available)
                .occupied(occupied)
                .reserved(reserved)
                .build();
    }

    @Transactional
    public SlotDTO toggleSlotStatus(Long slotId) {
        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));

        if (slot.getStatus() == ParkingSlot.SlotStatus.AVAILABLE) {
            slot.setStatus(ParkingSlot.SlotStatus.OCCUPIED);
            slot.setVehicleNo(generatePlate());
        } else {
            slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
            slot.setVehicleNo(null);
            slot.setBookedAt(null);
            slot.setExpiresAt(null);
        }

        slotRepository.save(slot);
        return toSlotDTO(slot, slot.getZone().getSlotsPerRow());
    }

    private String generatePlate() {
        String[] states = {"KA", "MH", "DL", "TN", "AP", "TS", "UP", "GJ", "RJ", "WB"};
        String st = states[(int) (Math.random() * states.length)];
        String d1 = String.format("%02d", (int) (Math.random() * 99) + 1);
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String l = "" + letters.charAt((int) (Math.random() * 26)) + letters.charAt((int) (Math.random() * 26));
        int d2 = (int) (Math.random() * 9000) + 1000;
        return st + "-" + d1 + "-" + l + "-" + d2;
    }

    private LocationDTO toLocationDTO(Location loc) {
        return LocationDTO.builder()
                .id(loc.getId())
                .code(loc.getCode())
                .name(loc.getName())
                .icon(loc.getIcon())
                .address(loc.getAddress())
                .color(loc.getColor())
                .floors(loc.getFloors().stream().map(f -> LocationDTO.FloorDTO.builder()
                        .id(f.getId())
                        .code(f.getCode())
                        .name(f.getName())
                        .zones(f.getZones().stream().map(z -> LocationDTO.ZoneDTO.builder()
                                .id(z.getId())
                                .code(z.getCode())
                                .name(z.getName())
                                .rows(z.getRows())
                                .slotsPerRow(z.getSlotsPerRow())
                                .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private SlotDTO toSlotDTO(ParkingSlot slot, int slotsPerRow) {
        return SlotDTO.builder()
                .id(slot.getId())
                .slotLabel(slot.getSlotLabel())
                .slotIndex(slot.getSlotIndex())
                .status(slot.getStatus().name().toLowerCase())
                .type(slot.getType().name().toLowerCase())
                .vehicleNo(slot.getVehicleNo())
                .bookedAt(slot.getBookedAt())
                .expiresAt(slot.getExpiresAt())
                .row(slotsPerRow > 0 ? slot.getSlotIndex() / slotsPerRow : 0)
                .col(slotsPerRow > 0 ? slot.getSlotIndex() % slotsPerRow : 0)
                .slotsPerRow(slotsPerRow)
                .build();
    }
}
