package com.smartpark.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot slot;

    @Column(nullable = false)
    private String vehicleNo;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private String locationName;
    private String floorName;
    private String zoneName;
    private String slotLabel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.ACTIVE;

    public enum BookingStatus {
        ACTIVE, EXPIRED, CANCELLED
    }
}
