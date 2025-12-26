package management.backend.inventory.entity;

/**
 * User role enumeration for role-based access control.
 * Defines all available roles in the system.
 */
public enum UserRoleEnum {
    ADMIN("ROLE_ADMIN", "Administrator - Full system access"),
    USER("ROLE_USER", "Regular User - Standard access");

    private final String authority;
    private final String description;

    UserRoleEnum(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get enum from authority string
     */
    public static UserRoleEnum fromAuthority(String authority) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown authority: " + authority);
    }

    /**
     * Get enum from name (for database storage)
     */
    public static UserRoleEnum fromName(String name) {
        try {
            return UserRoleEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + name);
        }
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if user has user role
     */
    public boolean isUser() {
        return this == USER;
    }
}
