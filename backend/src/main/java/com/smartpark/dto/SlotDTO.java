package com.smartpark.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SlotDTO {
    private Long id;
    private String slotLabel;
    private int slotIndex;
    private String status;
    private String type;
    private String vehicleNo;
    private LocalDateTime bookedAt;
    private LocalDateTime expiresAt;
    private int row;
    private int col;
    private int slotsPerRow;
}
