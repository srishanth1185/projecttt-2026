package com.smartpark.service;

import com.smartpark.dto.ActivityDTO;
import com.smartpark.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    public List<ActivityDTO> getRecentActivity(int limit) {
        return activityLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
                .stream()
                .map(a -> ActivityDTO.builder()
                        .id(a.getId())
                        .type(a.getType())
                        .message(a.getMessage())
                        .timestamp(a.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
