package com.smartpark.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityDTO {
    private Long id;
    private String type;
    private String message;
    private LocalDateTime timestamp;
}
