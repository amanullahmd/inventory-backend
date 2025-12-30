package management.backend.inventory.repository;

import management.backend.inventory.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByBatchNumber(String batchNumber);
    List<Batch> findByItemItemId(Long itemId);
    List<Batch> findByIsActive(Boolean isActive);
}
