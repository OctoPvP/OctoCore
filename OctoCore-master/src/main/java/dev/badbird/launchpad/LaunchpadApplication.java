package dev.badbird.launchpad;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import dev.badbird.launchpad.models.User;
import dev.badbird.launchpad.repository.MongoUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@Theme("LaunchPad")
@PWA(name = "Launchpad", shortName = "Launchpad") // TODO: icon
public class LaunchpadApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        System.out.println("Starting Launchpad with java v." + System.getProperty("java.version"));
        SpringApplication.run(LaunchpadApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, MongoUserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User admin = new User("Test", passwordEncoder.encode("123"));
            admin.setRoles(Stream.of("ROLE_ADMIN", "ROLE_USER").collect(Collectors.toSet()));
            userRepository.save(admin);
            User user = new User("User", passwordEncoder.encode("123"));
            user.setRoles(Stream.of("ROLE_USER").collect(Collectors.toSet()));
            userRepository.save(user);

            logger.info("Generated demo data");
        };
    }

}
