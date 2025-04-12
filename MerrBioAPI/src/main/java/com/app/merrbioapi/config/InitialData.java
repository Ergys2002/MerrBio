package com.app.merrbioapi.config;

import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.Gender;
import com.app.merrbioapi.model.enums.Role;
import com.app.merrbioapi.repository.UserInfoRepository;
import com.app.merrbioapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
public class InitialData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitialData(UserRepository userRepository,
                       UserInfoRepository userInfoRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminUser();
        System.out.println("Initial data loaded");
    }

    private void createAdminUser() {
        String adminEmail = "admin@merrbio.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("Admin123!"));
            adminUser.setRole(Role.ADMIN);

            User savedUser = userRepository.save(adminUser);

            UserInfo adminInfo = new UserInfo();
            adminInfo.setFirstName("Admin");
            adminInfo.setLastName("User");
            adminInfo.setPhoneNumber("+1234567890");
            adminInfo.setBirthDate(new Date());
            adminInfo.setGender(Gender.MALE);
            adminInfo.setUser(savedUser);

            userInfoRepository.save(adminInfo);

            System.out.println("Admin user created successfully");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}