package com.example.server.dto;

import lombok.Data;
import java.util.List;

@Data
public class MeterReadingRequest {
    private Long roomId;
    private String month;
    private List<ReadingItem> readings;

    @Data
    public static class ReadingItem {
        private Long serviceId;
        private Integer oldValue;
        private Integer newValue;
    }
}