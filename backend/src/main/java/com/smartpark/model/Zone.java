package com.smartpark.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // e.g. "A", "B", "EM", "VS"

    @Column(nullable = false)
    private String name;

    private int rows;
    private int slotsPerRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("slotIndex ASC")
    @Builder.Default
    private List<ParkingSlot> slots = new ArrayList<>();
}
