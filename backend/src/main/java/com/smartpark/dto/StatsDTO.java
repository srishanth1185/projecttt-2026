package com.smartpark.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StatsDTO {
    private long total;
    private long available;
    private long occupied;
    private long reserved;
}
