package dev.gneto.file_converter.config;

import dev.gneto.file_converter.enums.UserRole;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findUserByEmail(adminEmail).isPresent()) {
            return;
        }

        User admin = new User(
                "Administrador",
                "Sistema",
                adminEmail,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        );

        userRepository.save(admin);
    }
}
