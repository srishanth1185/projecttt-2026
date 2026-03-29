package com.smartpark.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDTO {
    private Long id;
    private Long slotId;
    private String slotLabel;
    private String vehicleNo;
    private LocalDateTime bookedAt;
    private LocalDateTime expiresAt;
    private String locationName;
    private String floorName;
    private String zoneName;
    private String bookingStatus;
}
