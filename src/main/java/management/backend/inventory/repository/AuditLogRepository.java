package management.backend.inventory.repository;

import management.backend.inventory.entity.AuditAction;
import management.backend.inventory.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entity operations.
 * Requirements: 5.4 - Audit trail repository for compliance and debugging
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Find all audit logs for a specific entity type.
     */
    List<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);
    
    /**
     * Find all audit logs for a specific entity.
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    
    /**
     * Find all audit logs for a specific user.
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all audit logs for a specific action.
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action);
    
    /**
     * Find audit logs within a date range.
     */
    @Query("""
        SELECT al FROM AuditLog al 
        WHERE al.createdAt BETWEEN :startDate AND :endDate 
        ORDER BY al.createdAt DESC
        """)
    List<AuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find recent audit logs.
     */
    @Query("""
        SELECT al FROM AuditLog al 
        ORDER BY al.createdAt DESC
        """)
    List<AuditLog> findRecentLogs();
    
    /**
     * Count audit logs for a specific entity type.
     */
    long countByEntityType(String entityType);
    
    /**
     * Count audit logs for a specific action.
     */
    long countByAction(AuditAction action);
}
