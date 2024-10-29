package harmo.projects.shoppingcart.data;

import harmo.projects.shoppingcart.model.Role;
import harmo.projects.shoppingcart.model.User;
import harmo.projects.shoppingcart.repository.RolesRepository;
import harmo.projects.shoppingcart.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
//        createDefaultUserIfNotExists();
//        createDefaultAdminIfNotExists();
//        createDefaultRoleIfNotExists(defaultRoles);

    }

    private void createDefaultUserIfNotExists() {
        Role userRole = rolesRepository.findByName("ROLE_USER").get();
        for(int i = 1; i <= 10;i++){
            String email = "user"+i+"@gmail.com";
            if(userRepository.existsByEmail(email)){
                continue;
            }
            User user = new User();
            user.setFirstName("The user");
            user.setLastName("User"+i);
            user.setEmail(email);
            user.setRoles(Set.of(userRole));
            user.setPassword(passwordEncoder.encode("password12d"+i));
            userRepository.save(user);
            log.info("User created: {}", user);
        }
    }
    private void createDefaultAdminIfNotExists() {
        Role adminRole = rolesRepository.findByName("ROLE_ADMIN").get();
        for(int i = 1; i <= 2;i++){
            String email = "admin"+i+"@gmail.com";
            if(userRepository.existsByEmail(email)){
                continue;
            }
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin"+i);
            user.setEmail(email);
            user.setRoles(Set.of(adminRole));
            user.setPassword(passwordEncoder.encode("password12d"+i));
            userRepository.save(user);
            log.info("Admin created: {}", user);
        }
    }
    private void createDefaultRoleIfNotExists(Set<String> roles) {
        roles.stream()
                .filter(role -> rolesRepository.findByName(role).isEmpty())
                .map(Role::new).forEach(rolesRepository::save);
    }
}
