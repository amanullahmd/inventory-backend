package management.backend.inventory.repository;

import management.backend.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity.
 * Requirements: 3.1, 3.2 - Category data access
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name (case-insensitive).
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Check if category exists by name.
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find all categories ordered by name.
     */
    List<Category> findAllByOrderByNameAsc();
}
