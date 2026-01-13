package management.backend.inventory.repository;

import management.backend.inventory.entity.UserProfile;
import management.backend.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUser(User user);
    
    Optional<UserProfile> findByUserId(Long userId);
    
    boolean existsByUser(User user);
}
