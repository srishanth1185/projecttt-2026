package com.smartpark.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_slots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String slotLabel; // e.g. "A1-3"

    @Column(nullable = false)
    private int slotIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SlotType type = SlotType.CAR;

    private String vehicleNo;
    private LocalDateTime bookedAt;
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    public enum SlotStatus {
        AVAILABLE, OCCUPIED, RESERVED
    }

    public enum SlotType {
        CAR, HANDICAPPED
    }
}
