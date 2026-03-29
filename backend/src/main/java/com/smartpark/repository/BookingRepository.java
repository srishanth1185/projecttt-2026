package com.smartpark.repository;

import com.smartpark.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'ACTIVE' AND b.expiresAt > :now ORDER BY b.bookedAt DESC")
    List<Booking> findActiveBookings(@Param("now") LocalDateTime now);

    List<Booking> findAllByOrderByBookedAtDesc();

    @Query("SELECT b FROM Booking b WHERE b.slot.id = :slotId AND b.bookingStatus = 'ACTIVE' AND b.expiresAt > :now")
    List<Booking> findActiveBySlotId(@Param("slotId") Long slotId, @Param("now") LocalDateTime now);
}
