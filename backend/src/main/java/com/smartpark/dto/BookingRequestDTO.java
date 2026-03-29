package com.smartpark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingRequestDTO {
    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNo;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private int durationMinutes = 30;
}
