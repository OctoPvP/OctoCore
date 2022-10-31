package net.octopvp.octocore.master;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@Theme("OctoCore")
@PWA(name = "OctoCore Master", shortName = "Master", iconPath = "img/logo.png")
@Push
public class MasterApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        System.out.println("Starting OctoCore Master with java v." + System.getProperty("java.version"));
        boolean dev = Objects.equals(System.getProperty("spring.profiles.active"), "dev");
        System.out.println("DEV: " + dev);
        SpringApplication.run(MasterApplication.class, args);
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
