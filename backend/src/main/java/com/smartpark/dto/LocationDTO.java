package com.smartpark.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LocationDTO {
    private Long id;
    private String code;
    private String name;
    private String icon;
    private String address;
    private String color;
    private List<FloorDTO> floors;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FloorDTO {
        private Long id;
        private String code;
        private String name;
        private List<ZoneDTO> zones;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ZoneDTO {
        private Long id;
        private String code;
        private String name;
        private int rows;
        private int slotsPerRow;
    }
}
