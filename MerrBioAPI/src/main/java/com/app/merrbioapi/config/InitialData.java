package com.app.merrbioapi.config;

import com.app.merrbioapi.model.entity.Farmer; // Import Farmer
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.Gender;
import com.app.merrbioapi.model.enums.Role;
import com.app.merrbioapi.repository.FarmerRepository; // Import FarmerRepository
import com.app.merrbioapi.repository.UserInfoRepository;
import com.app.merrbioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor; // Use Lombok for constructor injection
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // Optional but good practice

import java.util.Calendar;
import java.util.Date;

@Configuration
@RequiredArgsConstructor // Use Lombok for cleaner constructor injection
public class InitialData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final FarmerRepository farmerRepository; // Inject FarmerRepository
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Optional: Makes the whole run method transactional
    public void run(String... args) throws Exception {
        System.out.println("Loading initial data...");
        createAdminUser();
        createCustomerUser();
        createFarmerUser();
        System.out.println("Initial data loading complete.");
    }

    private void createAdminUser() {
        String adminEmail = "admin@merrbio.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123!")) // Use a strong password
                    .role(Role.ADMIN)
                    .build();
            User savedUser = userRepository.save(adminUser);

            UserInfo adminInfo = UserInfo.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .phoneNumber("0000000000") // Use a placeholder or unique number
                    .birthDate(getYesterdaysDate()) // Example birth date
                    .gender(Gender.MALE)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(adminInfo);

            System.out.println("-> Admin user created successfully");
        } else {
            System.out.println("-> Admin user already exists");
        }
    }

    private void createCustomerUser() {
        String customerEmail = "customer@merrbio.com";
        if (!userRepository.existsByEmail(customerEmail)) {
            User customerUser = User.builder()
                    .email(customerEmail)
                    .password(passwordEncoder.encode("Customer123!")) // Use a strong password
                    .role(Role.CUSTOMER)
                    .build();
            User savedUser = userRepository.save(customerUser);

            UserInfo customerInfo = UserInfo.builder()
                    .firstName("Regular")
                    .lastName("Customer")
                    .phoneNumber("1111111111") // Use a placeholder or unique number
                    .birthDate(getYesterdaysDate()) // Example birth date
                    .gender(Gender.FEMALE)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(customerInfo);

            System.out.println("-> Customer user created successfully");
        } else {
            System.out.println("-> Customer user already exists");
        }
    }

    private void createFarmerUser() {
        String farmerEmail = "farmer@merrbio.com";
        if (!userRepository.existsByEmail(farmerEmail)) {
            // 1. Create User
            User farmerUser = User.builder()
                    .email(farmerEmail)
                    .password(passwordEncoder.encode("Farmer123!")) // Use a strong password
                    .role(Role.FARMER)
                    .build();
            User savedUser = userRepository.save(farmerUser);

            // 2. Create UserInfo
            UserInfo farmerInfo = UserInfo.builder()
                    .firstName("Organic")
                    .lastName("Farmer")
                    .phoneNumber("2222222222") // Use a placeholder or unique number
                    .birthDate(getYesterdaysDate()) // Example birth date
                    .gender(Gender.MALE)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(farmerInfo);

            // 3. Create Farmer
            Farmer farmer = Farmer.builder()
                    .user(savedUser) // Link to the saved user
                    .farmName("Green Acres Farm")
                    .farmLocation("Valley Meadow, Countryside")
                    .bio("Providing the freshest organic produce for over 10 years.")
                    .isVerified(false) // Typically starts as unverified
                    .build();
            farmerRepository.save(farmer);

            System.out.println("-> Farmer user created successfully");
        } else {
            System.out.println("-> Farmer user already exists");
        }
    }

    // Helper to get a consistent past date
    private Date getYesterdaysDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1); // Set to yesterday
        cal.set(Calendar.YEAR, 1990); // Or set a specific year
        return cal.getTime();
    }
}