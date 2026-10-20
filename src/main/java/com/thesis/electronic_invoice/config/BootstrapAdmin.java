package com.thesis.electronic_invoice.config;

import com.thesis.electronic_invoice.constant.UserRole;
import com.thesis.electronic_invoice.model.MstUsers;
import com.thesis.electronic_invoice.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class BootstrapAdmin {
    @Bean
    CommandLineRunner createInitialAdmin(UsersRepository users, PasswordEncoder encoder,
                                         @Value("${app.bootstrap-admin.username:admin}") String username,
                                         @Value("${app.bootstrap-admin.password:}") String password,
                                         @Value("${app.bootstrap-admin.first-name:System}") String firstName) {
        return args -> {
            if (users.count() == 0) {
                if (password.length() < 8)
                    throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD must be at least 8 characters");
                MstUsers admin = new MstUsers();
                admin.setUsername(username);
                admin.setPassword(encoder.encode(password));
                admin.setUserRole(UserRole.ADMIN);
                admin.setFirstName(firstName);
                admin.setCreatedBy("system");
                admin.setCreatedDt(LocalDateTime.now());
                users.save(admin);
            }
        };
    }
}
