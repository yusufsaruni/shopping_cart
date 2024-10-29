package harmo.projects.shoppingcart.repository;

import harmo.projects.shoppingcart.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String role);
}
