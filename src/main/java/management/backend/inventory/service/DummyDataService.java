package management.backend.inventory.service;

import lombok.RequiredArgsConstructor;
import management.backend.inventory.entity.Category;
import management.backend.inventory.entity.Item;
import management.backend.inventory.repository.CategoryRepository;
import management.backend.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for generating dummy inventory data for testing and development.
 * Requirements: 5.1, 5.2, 5.3, 5.4
 */
@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Add dummy inventory data.
     * Creates 5 categories and 30 items across those categories with realistic data.
     * Requirements: 5.1, 5.2, 5.3, 5.4
     */
    @Transactional
    public int addDummyData() {
        // Check if dummy data already exists (idempotency)
        if (itemRepository.existsBySku("MBP-16-001")) {
            return 0; // Dummy data already exists
        }

        // Create categories first
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Laptops", "Portable computers", "#3B82F6"));
        categories.add(new Category("Peripherals", "Mice, keyboards, cables", "#8B5CF6"));
        categories.add(new Category("Monitors", "Display screens", "#10B981"));
        categories.add(new Category("Audio", "Microphones and speakers", "#F97316"));
        categories.add(new Category("Accessories", "Desk accessories and stands", "#EC4899"));
        
        categoryRepository.saveAll(categories);

        List<Item> items = new ArrayList<>();

        // Laptops (5 items)
        items.add(new Item("MacBook Pro 16\"", "MBP-16-001", new BigDecimal("2499.99")));
        items.add(new Item("Dell XPS 13", "DXP-13-002", new BigDecimal("1299.99")));
        items.add(new Item("HP Pavilion 15", "HPP-15-003", new BigDecimal("799.99")));
        items.add(new Item("Lenovo ThinkPad", "LTP-X1-004", new BigDecimal("1199.99")));
        items.add(new Item("ASUS VivoBook", "ASV-15-005", new BigDecimal("649.99")));

        // Peripherals (11 items)
        items.add(new Item("Logitech MX Master 3", "LGM-MX3-006", new BigDecimal("99.99")));
        items.add(new Item("Razer DeathAdder V3", "RZR-DA3-007", new BigDecimal("69.99")));
        items.add(new Item("SteelSeries Rival 600", "STS-R600-008", new BigDecimal("79.99")));
        items.add(new Item("USB-C Cable 2m", "USB-C2M-009", new BigDecimal("12.99")));
        items.add(new Item("HDMI 2.1 Cable", "HDMI-21-010", new BigDecimal("19.99")));
        items.add(new Item("DisplayPort Cable", "DP-14-011", new BigDecimal("24.99")));
        items.add(new Item("Mechanical Keyboard RGB", "MKB-RGB-016", new BigDecimal("149.99")));
        items.add(new Item("Keychron K8 Pro", "KCH-K8P-017", new BigDecimal("129.99")));
        items.add(new Item("Corsair K95 Platinum", "COR-K95-018", new BigDecimal("199.99")));
        items.add(new Item("Logitech MX Keys", "LGM-MXK-019", new BigDecimal("99.99")));

        // Monitors (4 items)
        items.add(new Item("LG UltraWide 34\"", "LG-UW34-012", new BigDecimal("799.99")));
        items.add(new Item("Dell S3422DWG", "DLS-34-013", new BigDecimal("699.99")));
        items.add(new Item("ASUS ProArt PA278QV", "ASP-PA27-014", new BigDecimal("549.99")));
        items.add(new Item("BenQ EW2780U", "BNQ-EW27-015", new BigDecimal("449.99")));

        // Audio (6 items)
        items.add(new Item("Webcam Logitech 4K", "LGW-4K-020", new BigDecimal("149.99")));
        items.add(new Item("Razer Kiyo Pro", "RZR-KP-021", new BigDecimal("199.99")));
        items.add(new Item("Elgato Facecam", "ELG-FC-022", new BigDecimal("179.99")));
        items.add(new Item("Blue Yeti Microphone", "BLU-YET-023", new BigDecimal("99.99")));
        items.add(new Item("Audio-Technica AT2020", "ATA-2020-024", new BigDecimal("99.00")));
        items.add(new Item("Shure SM7B", "SHR-SM7B-025", new BigDecimal("399.00")));

        // Accessories (4 items)
        items.add(new Item("Desk Lamp LED RGB", "DLM-RGB-026", new BigDecimal("59.99")));
        items.add(new Item("Monitor Arm Dual", "MNA-DUL-027", new BigDecimal("79.99")));
        items.add(new Item("Laptop Stand Aluminum", "LPS-ALU-028", new BigDecimal("49.99")));
        items.add(new Item("Phone Stand Premium", "PHS-PRM-029", new BigDecimal("29.99")));
        items.add(new Item("Desk Pad XL", "DSP-XL-030", new BigDecimal("39.99")));

        // Save all items
        itemRepository.saveAll(items);

        return items.size();
    }

    /**
     * Check if dummy data exists.
     */
    public boolean dummyDataExists() {
        return itemRepository.existsBySku("MBP-16-001");
    }

    /**
     * Clear all dummy data.
     */
    @Transactional
    public void clearDummyData() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
