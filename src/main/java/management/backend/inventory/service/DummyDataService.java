package management.backend.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import management.backend.inventory.repository.CategoryRepository;
import management.backend.inventory.repository.ItemRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service for generating dummy inventory data for testing and development.
 * DISABLED: Dummy data has been removed for production safety.
 * This service is kept for reference only.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DummyDataService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Dummy data loading is disabled.
     * @deprecated Use proper data seeding through API or manual database operations
     */
    @Deprecated
    public int addDummyData() {
        log.warn("⚠️ DummyDataService.addDummyData() is deprecated and disabled");
        return 0;
    }

    /**
     * Check if dummy data exists.
     */
    public boolean dummyDataExists() {
        return false;
    }

    /**
     * Clear all dummy data.
     */
    public void clearDummyData() {
        log.info("DummyDataService.clearDummyData() is disabled");
    }
}
