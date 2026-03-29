package com.smartpark.repository;

import com.smartpark.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Query("SELECT s FROM ParkingSlot s WHERE s.zone.floor.location.code = :locationCode")
    List<ParkingSlot> findByLocationCode(@Param("locationCode") String locationCode);

    @Query("SELECT s FROM ParkingSlot s WHERE s.zone.floor.location.code = :locationCode " +
           "AND s.zone.floor.code = :floorCode AND s.zone.code = :zoneCode ORDER BY s.slotIndex")
    List<ParkingSlot> findByLocationAndFloorAndZone(
            @Param("locationCode") String locationCode,
            @Param("floorCode") String floorCode,
            @Param("zoneCode") String zoneCode);

    @Query("SELECT s FROM ParkingSlot s WHERE s.status = 'RESERVED' AND s.expiresAt <= :now")
    List<ParkingSlot> findExpiredReservations(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) FROM ParkingSlot s WHERE s.zone.floor.location.code = :locationCode AND s.status = :status")
    long countByLocationCodeAndStatus(@Param("locationCode") String locationCode, @Param("status") ParkingSlot.SlotStatus status);

    @Query("SELECT COUNT(s) FROM ParkingSlot s WHERE s.zone.floor.location.code = :locationCode")
    long countByLocationCode(@Param("locationCode") String locationCode);

    @Query("SELECT COUNT(s) FROM ParkingSlot s WHERE s.status = :status")
    long countByStatus(@Param("status") ParkingSlot.SlotStatus status);
}
