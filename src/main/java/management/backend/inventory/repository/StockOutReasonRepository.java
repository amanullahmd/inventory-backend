package management.backend.inventory.repository;

import management.backend.inventory.entity.StockOutReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockOutReasonRepository extends JpaRepository<StockOutReason, Long> {
    
    List<StockOutReason> findByIsActiveTrue();
    
    Optional<StockOutReason> findByReasonName(String reasonName);
    
    boolean existsByReasonName(String reasonName);
}
