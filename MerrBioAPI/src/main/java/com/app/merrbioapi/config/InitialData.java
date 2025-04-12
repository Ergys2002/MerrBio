package com.app.merrbioapi.config;

import com.app.merrbioapi.model.entity.Category;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.Gender;
import com.app.merrbioapi.model.enums.Role;
import com.app.merrbioapi.repository.CategoryRepository;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.UserInfoRepository;
import com.app.merrbioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class InitialData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final FarmerRepository farmerRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Loading initial data...");
        createAdminUser();
        createCustomerUsers();
        createFarmerUsers();
        createCategories();
        System.out.println("Initial data loading complete.");
    }

    private void createAdminUser() {
        String adminEmail = "admin@merrbio.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123!"))
                    .role(Role.ADMIN)
                    .build();
            User savedUser = userRepository.save(adminUser);

            UserInfo adminInfo = UserInfo.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .phoneNumber("0000000000")
                    .birthDate(getRandomDate(1980, 1990))
                    .gender(Gender.MALE)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(adminInfo);

            System.out.println("-> Admin user created successfully");
        } else {
            System.out.println("-> Admin user already exists");
        }
    }

    private void createCustomerUsers() {
        // Original customer
        createCustomer(
                "customer@merrbio.com", 
                "Customer123!", 
                "Regular", 
                "Customer", 
                "1111111111",
                Gender.FEMALE
        );
        
        // Additional customers
        List<Map<String, Object>> customers = new ArrayList<>();
        
        customers.add(Map.of(
            "email", "john.doe@example.com",
            "password", "JohnDoe123!",
            "firstName", "John",
            "lastName", "Doe",
            "phone", "2223334444",
            "gender", Gender.MALE
        ));
        
        customers.add(Map.of(
            "email", "maria.garcia@example.com",
            "password", "MariaG123!",
            "firstName", "Maria",
            "lastName", "Garcia",
            "phone", "3334445555",
            "gender", Gender.FEMALE
        ));
        
        customers.add(Map.of(
            "email", "ali.ahmed@example.com",
            "password", "AliAhmed123!",
            "firstName", "Ali",
            "lastName", "Ahmed",
            "phone", "4445556666",
            "gender", Gender.MALE
        ));
        
        customers.add(Map.of(
            "email", "emma.wilson@example.com",
            "password", "EmmaW123!",
            "firstName", "Emma",
            "lastName", "Wilson",
            "phone", "5556667777",
            "gender", Gender.FEMALE
        ));
        
        // Create all additional customers
        for (Map<String, Object> customer : customers) {
            createCustomer(
                (String) customer.get("email"),
                (String) customer.get("password"),
                (String) customer.get("firstName"),
                (String) customer.get("lastName"),
                (String) customer.get("phone"),
                (Gender) customer.get("gender")
            );
        }
    }
    
    private void createCustomer(String email, String password, String firstName, String lastName, String phone, Gender gender) {
        if (!userRepository.existsByEmail(email)) {
            User customerUser = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.CUSTOMER)
                    .build();
            User savedUser = userRepository.save(customerUser);

            UserInfo customerInfo = UserInfo.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .phoneNumber(phone)
                    .birthDate(getRandomDate(1970, 2000))
                    .gender(gender)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(customerInfo);

            System.out.println("-> Customer user created: " + email);
        } else {
            System.out.println("-> Customer user already exists: " + email);
        }
    }

    private void createFarmerUsers() {
        // Original farmer
        createFarmer(
                "farmer@merrbio.com",
                "Farmer123!",
                "Organic",
                "Farmer",
                "2222222222",
                Gender.MALE,
                "Green Acres Farm",
                "Valley Meadow, Countryside",
                "Providing the freshest organic produce for over 10 years.",
                false
        );
        
        // Additional farmers
        List<Map<String, Object>> farmers = new ArrayList<>();
        
        farmers.add(Map.of(
            "email", "sofia.lopez@example.com",
            "password", "SofiaL123!",
            "firstName", "Sofia",
            "lastName", "Lopez",
            "phone", "6667778888",
            "gender", Gender.FEMALE,
            "farmName", "Sunflower Fields",
            "location", "Southern Hills, Farmland",
            "bio", "Family-owned organic farm specializing in heirloom vegetables and herbs.",
            "verified", true
        ));
        
        farmers.add(Map.of(
            "email", "mikhail.petrov@example.com",
            "password", "MikhailP123!",
            "firstName", "Mikhail",
            "lastName", "Petrov",
            "phone", "7778889999",
            "gender", Gender.MALE,
            "farmName", "Heritage Roots",
            "location", "Northern Valley, Rural District",
            "bio", "Traditional farming techniques with a focus on biodiversity and sustainability.",
            "verified", true
        ));
        
        farmers.add(Map.of(
            "email", "amara.okafor@example.com",
            "password", "AmaraO123!",
            "firstName", "Amara",
            "lastName", "Okafor",
            "phone", "8889990000",
            "gender", Gender.FEMALE,
            "farmName", "Fertile Ground Farms",
            "location", "Eastern Plateau, Agricultural Zone",
            "bio", "Pioneering innovative organic farming methods for maximum nutrition and flavor.",
            "verified", false
        ));
        
        // Create all additional farmers
        for (Map<String, Object> farmer : farmers) {
            createFarmer(
                (String) farmer.get("email"),
                (String) farmer.get("password"),
                (String) farmer.get("firstName"),
                (String) farmer.get("lastName"),
                (String) farmer.get("phone"),
                (Gender) farmer.get("gender"),
                (String) farmer.get("farmName"),
                (String) farmer.get("location"),
                (String) farmer.get("bio"),
                (Boolean) farmer.get("verified")
            );
        }
    }
    
    private void createFarmer(
            String email, String password, String firstName, String lastName, 
            String phone, Gender gender, String farmName, String location, String bio, Boolean verified) {
        if (!userRepository.existsByEmail(email)) {
            User farmerUser = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.FARMER)
                    .build();
            User savedUser = userRepository.save(farmerUser);

            UserInfo farmerInfo = UserInfo.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .phoneNumber(phone)
                    .birthDate(getRandomDate(1965, 1995))
                    .gender(gender)
                    .user(savedUser)
                    .build();
            userInfoRepository.save(farmerInfo);

            Farmer farmer = Farmer.builder()
                    .user(savedUser)
                    .farmName(farmName)
                    .farmLocation(location)
                    .bio(bio)
                    .isVerified(verified)
                    .build();
            farmerRepository.save(farmer);

            System.out.println("-> Farmer user created: " + email);
        } else {
            System.out.println("-> Farmer user already exists: " + email);
        }
    }
    
    private void createCategories() {
        if (categoryRepository.count() == 0) {
            List<String> categoryNames = Arrays.asList(
                "Vegetables",
                "Fruits",
                "Herbs",
                "Dairy",
                "Eggs",
                "Meat",
                "Honey & Bee Products",
                "Nuts & Seeds",
                "Grains",
                "Specialty Items",
                "Preserves & Jams",
                "Seasonal Produce"
            );
            
            List<Category> categories = new ArrayList<>();
            for (String name : categoryNames) {
                Category category = Category.builder()
                        .name(name)
                        .description("Organic " + name.toLowerCase() + " grown with sustainable farming practices")
                        .build();
                categories.add(category);
            }
            
            categoryRepository.saveAll(categories);
            System.out.println("-> Created " + categories.size() + " product categories");
        } else {
            System.out.println("-> Categories already exist");
        }
    }

    // Helper to get a random date within range
    private Date getRandomDate(int minYear, int maxYear) {
        Calendar cal = Calendar.getInstance();
        int year = minYear + new Random().nextInt(maxYear - minYear + 1);
        int month = new Random().nextInt(12);
        int day = new Random().nextInt(28) + 1; // Avoid invalid dates
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        
        return cal.getTime();
    }
    
    // Original helper method - keeping for compatibility
    private Date getYesterdaysDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1); 
        cal.set(Calendar.YEAR, 1990); 
        return cal.getTime();
    }
}