package management.backend.inventory.repository;

import management.backend.inventory.entity.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {
    List<Demand> findAllByOrderByCreatedAtDesc();
}
