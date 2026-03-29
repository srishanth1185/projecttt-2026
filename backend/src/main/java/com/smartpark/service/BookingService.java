package com.smartpark.service;

import com.smartpark.dto.BookingDTO;
import com.smartpark.dto.BookingRequestDTO;
import com.smartpark.model.*;
import com.smartpark.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public BookingDTO createBooking(BookingRequestDTO request) {
        ParkingSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() != ParkingSlot.SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot is not available");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(request.getDurationMinutes());

        slot.setStatus(ParkingSlot.SlotStatus.RESERVED);
        slot.setVehicleNo(request.getVehicleNo().toUpperCase());
        slot.setBookedAt(now);
        slot.setExpiresAt(expiresAt);
        slotRepository.save(slot);

        Zone zone = slot.getZone();
        Floor floor = zone.getFloor();
        Location location = floor.getLocation();

        Booking booking = Booking.builder()
                .slot(slot)
                .vehicleNo(request.getVehicleNo().toUpperCase())
                .bookedAt(now)
                .expiresAt(expiresAt)
                .locationName(location.getName())
                .floorName(floor.getName())
                .zoneName(zone.getName())
                .slotLabel(slot.getSlotLabel())
                .bookingStatus(Booking.BookingStatus.ACTIVE)
                .build();

        bookingRepository.save(booking);

        // Log activity
        activityLogRepository.save(ActivityLog.builder()
                .type("booked")
                .message("Slot " + slot.getSlotLabel() + " reserved for " + request.getVehicleNo().toUpperCase())
                .timestamp(now)
                .build());

        return toBookingDTO(booking);
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAllByOrderByBookedAtDesc().stream()
                .map(this::toBookingDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getActiveBookings() {
        return bookingRepository.findActiveBookings(LocalDateTime.now()).stream()
                .map(this::toBookingDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void releaseBooking(Long slotId) {
        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
        slot.setVehicleNo(null);
        slot.setBookedAt(null);
        slot.setExpiresAt(null);
        slotRepository.save(slot);

        // Mark active bookings for this slot as cancelled
        List<Booking> activeBookings = bookingRepository.findActiveBySlotId(slotId, LocalDateTime.now());
        activeBookings.forEach(b -> {
            b.setBookingStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(b);
        });

        activityLogRepository.save(ActivityLog.builder()
                .type("released")
                .message("Slot " + slot.getSlotLabel() + " released and is now available")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void cleanExpiredReservations() {
        List<ParkingSlot> expired = slotRepository.findExpiredReservations(LocalDateTime.now());
        for (ParkingSlot slot : expired) {
            slot.setStatus(ParkingSlot.SlotStatus.AVAILABLE);
            slot.setVehicleNo(null);
            slot.setBookedAt(null);
            slot.setExpiresAt(null);
            slotRepository.save(slot);

            // Mark bookings expired
            List<Booking> bookings = bookingRepository.findActiveBySlotId(slot.getId(), LocalDateTime.now());
            bookings.forEach(b -> {
                b.setBookingStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(b);
            });
        }
    }

    private BookingDTO toBookingDTO(Booking b) {
        return BookingDTO.builder()
                .id(b.getId())
                .slotId(b.getSlot().getId())
                .slotLabel(b.getSlotLabel())
                .vehicleNo(b.getVehicleNo())
                .bookedAt(b.getBookedAt())
                .expiresAt(b.getExpiresAt())
                .locationName(b.getLocationName())
                .floorName(b.getFloorName())
                .zoneName(b.getZoneName())
                .bookingStatus(b.getBookingStatus().name().toLowerCase())
                .build();
    }
}
