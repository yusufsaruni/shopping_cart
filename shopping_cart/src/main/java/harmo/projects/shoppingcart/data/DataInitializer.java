package harmo.projects.shoppingcart.data;

import harmo.projects.shoppingcart.model.User;
import harmo.projects.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultUserIfNotExists();
    }

    private void createDefaultUserIfNotExists() {
//        for(int i = 1; i <= 10;i++){
//            String email = "user"+i+"@gmail.com";
//            if(userRepository.existsByEmail(email)){
//                continue;
//            }
//            User user = new User();
//            user.setFirstName("The user");
//            user.setLastName("User"+i);
//            user.setEmail(email);
//            user.setPassword("password12d"+i);
//            userRepository.save(user);
//            log.info("User created: {}", user);
//        }
    }
}
