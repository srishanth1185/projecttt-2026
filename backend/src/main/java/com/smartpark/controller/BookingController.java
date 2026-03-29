package com.smartpark.controller;

import com.smartpark.dto.BookingDTO;
import com.smartpark.dto.BookingRequestDTO;
import com.smartpark.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BookingDTO>> getActiveBookings() {
        return ResponseEntity.ok(bookingService.getActiveBookings());
    }

    @PostMapping("/{slotId}/release")
    public ResponseEntity<Void> releaseBooking(@PathVariable Long slotId) {
        bookingService.releaseBooking(slotId);
        return ResponseEntity.ok().build();
    }
}
