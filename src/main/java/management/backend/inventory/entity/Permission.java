package management.backend.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name; // e.g., ITEM_CREATE

    @Column(name = "description")
    private String description;

    @Column(name = "module", nullable = false)
    private String module; // e.g., INVENTORY, USERS
}
