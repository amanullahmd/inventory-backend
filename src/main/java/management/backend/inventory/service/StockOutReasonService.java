package management.backend.inventory.service;

import management.backend.inventory.dto.StockOutReasonResponse;
import management.backend.inventory.entity.StockOutReasonEnum;
import management.backend.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for stock-out reason management and reporting.
 * Provides methods to track, analyze, and report on stock-out reasons.
 * 
 * Requirements: SaaS Features - Stock-out reasons tracking and reporting
 */
@Service
@Transactional
public class StockOutReasonService {
    
    private final StockMovementRepository stockMovementRepository;
    
    public StockOutReasonService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }
    
    /**
     * Get all predefined stock-out reasons.
     * 
     * @return list of all predefined reasons
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getPredefinedReasons() {
        return Arrays.stream(StockOutReasonEnum.values())
            .map(reason -> new StockOutReasonResponse(
                reason.name(),
                reason.getLabel(),
                reason.getDescription(),
                0,
                0.0
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Get stock-out reasons breakdown for a date range.
     * Returns count and percentage for each reason type.
     * 
     * @param startDate start date for filtering
     * @param endDate end date for filtering
     * @return list of reason breakdowns with counts and percentages
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getReasonBreakdown(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get all stock-out movements for the date range
        List<StockMovementRepository.ReasonBreakdownProjection> projections = stockMovementRepository
            .getReasonBreakdown(startDateTime, endDateTime);
        
        List<StockOutReasonResponse> reasonCounts = projections.stream()
            .map(p -> new StockOutReasonResponse(
                p.getReasonType(),
                getReasonLabel(p.getReasonType()),
                p.getCount()
            ))
            .collect(Collectors.toList());
        
        // Calculate total
        Integer total = reasonCounts.stream()
            .mapToInt(StockOutReasonResponse::getCount)
            .sum();
        
        // Calculate percentages
        if (total > 0) {
            reasonCounts.forEach(reason -> {
                double percentage = (reason.getCount() * 100.0) / total;
                reason.setPercentage(percentage);
            });
        }
        
        return reasonCounts;
    }
    
    /**
     * Get stock-out reasons breakdown for a specific item.
     * 
     * @param itemId the item ID
     * @param startDate start date for filtering
     * @param endDate end date for filtering
     * @return list of reason breakdowns for the item
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getReasonBreakdownByItem(Long itemId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<StockMovementRepository.ReasonBreakdownProjection> projections = stockMovementRepository
            .getReasonBreakdownByItem(itemId, startDateTime, endDateTime);
        
        List<StockOutReasonResponse> reasonCounts = projections.stream()
            .map(p -> new StockOutReasonResponse(
                p.getReasonType(),
                getReasonLabel(p.getReasonType()),
                p.getCount()
            ))
            .collect(Collectors.toList());
        
        // Calculate total
        Integer total = reasonCounts.stream()
            .mapToInt(StockOutReasonResponse::getCount)
            .sum();
        
        // Calculate percentages
        if (total > 0) {
            reasonCounts.forEach(reason -> {
                double percentage = (reason.getCount() * 100.0) / total;
                reason.setPercentage(percentage);
            });
        }
        
        return reasonCounts;
    }
    
    /**
     * Get stock-out reasons breakdown for a specific category.
     * 
     * @param categoryId the category ID
     * @param startDate start date for filtering
     * @param endDate end date for filtering
     * @return list of reason breakdowns for the category
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getReasonBreakdownByCategory(Long categoryId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<StockMovementRepository.ReasonBreakdownProjection> projections = stockMovementRepository
            .getReasonBreakdownByCategory(categoryId, startDateTime, endDateTime);
        
        List<StockOutReasonResponse> reasonCounts = projections.stream()
            .map(p -> new StockOutReasonResponse(
                p.getReasonType(),
                getReasonLabel(p.getReasonType()),
                p.getCount()
            ))
            .collect(Collectors.toList());
        
        // Calculate total
        Integer total = reasonCounts.stream()
            .mapToInt(StockOutReasonResponse::getCount)
            .sum();
        
        // Calculate percentages
        if (total > 0) {
            reasonCounts.forEach(reason -> {
                double percentage = (reason.getCount() * 100.0) / total;
                reason.setPercentage(percentage);
            });
        }
        
        return reasonCounts;
    }
    
    /**
     * Get all reason counts across all time.
     * 
     * @return map of reason type to count
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getReasonCounts() {
        Map<String, Integer> counts = new HashMap<>();
        
        for (StockOutReasonEnum reason : StockOutReasonEnum.values()) {
            Integer count = stockMovementRepository.countByReasonType(reason.name());
            counts.put(reason.name(), count != null ? count : 0);
        }
        
        return counts;
    }
    
    /**
     * Validate if a reason is valid (either predefined or custom).
     * 
     * @param reason the reason to validate
     * @return true if valid, false otherwise
     */
    public boolean validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return false;
        }
        
        // Check if it's a predefined reason
        if (StockOutReasonEnum.isValid(reason)) {
            return true;
        }
        
        // If it's "OTHER", custom reason is allowed
        if ("OTHER".equals(reason)) {
            return true;
        }
        
        // Custom reasons are allowed (max 100 chars)
        return reason.length() <= 100;
    }
    
    /**
     * Validate if a reason type is a valid enum value.
     * 
     * @param reasonType the reason type to validate
     * @return true if valid, false otherwise
     */
    public boolean validateReasonType(String reasonType) {
        return StockOutReasonEnum.isValid(reasonType);
    }
    
    /**
     * Get the reason type enum from a string.
     * 
     * @param reasonType the reason type string
     * @return the enum value or null if invalid
     */
    public StockOutReasonEnum getReasonTypeEnum(String reasonType) {
        if (reasonType == null) {
            return null;
        }
        try {
            return StockOutReasonEnum.valueOf(reasonType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Get the most common stock-out reasons.
     * 
     * @param limit number of top reasons to return
     * @return list of top reasons
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getTopReasons(int limit) {
        List<StockMovementRepository.ReasonBreakdownProjection> projections = stockMovementRepository.getTopReasons(limit);
        
        List<StockOutReasonResponse> allReasons = projections.stream()
            .map(p -> new StockOutReasonResponse(
                p.getReasonType(),
                getReasonLabel(p.getReasonType()),
                p.getCount()
            ))
            .collect(Collectors.toList());
        
        // Calculate total
        Integer total = allReasons.stream()
            .mapToInt(StockOutReasonResponse::getCount)
            .sum();
        
        // Calculate percentages
        if (total > 0) {
            allReasons.forEach(reason -> {
                double percentage = (reason.getCount() * 100.0) / total;
                reason.setPercentage(percentage);
            });
        }
        
        return allReasons;
    }
    
    /**
     * Get stock-out reasons for a specific date.
     * 
     * @param date the date to filter by
     * @return list of reasons for that date
     */
    @Transactional(readOnly = true)
    public List<StockOutReasonResponse> getReasonsByDate(LocalDate date) {
        return getReasonBreakdown(date, date);
    }
    
    /**
     * Get the label for a reason type.
     * 
     * @param reasonType the reason type enum name
     * @return the label or the reason type if not found
     */
    private String getReasonLabel(String reasonType) {
        if (reasonType == null) {
            return "Unknown";
        }
        try {
            StockOutReasonEnum reason = StockOutReasonEnum.valueOf(reasonType);
            return reason.getLabel();
        } catch (IllegalArgumentException e) {
            return reasonType;
        }
    }
}
