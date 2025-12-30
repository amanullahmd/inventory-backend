package management.backend.inventory.repository;

import management.backend.inventory.entity.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPriceRepository extends JpaRepository<ItemPrice, Long> {
    List<ItemPrice> findByItemItemId(Long itemId);
    List<ItemPrice> findByItemItemIdAndIsActive(Long itemId, Boolean isActive);
}
