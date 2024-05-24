package by.bntu.laboratory;

import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RoleRepository;
import by.bntu.laboratory.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class LaboratoryApplication {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LaboratoryApplication(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(LaboratoryApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Код инициализации
        createAndSaveRoles();
        createDefaultAdmin();
        createDefaultWriter();
    }

    private void createAndSaveRoles() {
        // Проверяем, есть ли уже роли в базе данных
        if (roleRepository.count() == 0) {
            // Создаем основные роли и сохраняем их
            Role adminRole = new Role("Admin");
            Role userRole = new Role("User");
            Role writerRole = new Role("Writer");
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            roleRepository.save(writerRole);
        }
    }
    private void createDefaultWriter() {
        // Проверяем, есть ли уже администратор в базе данных
        if (userRepository.count() == 1) {
            // Находим роли "Admin" и "User" в базе данных
            Role writerRole = roleRepository.findByName("Writer");
            Role userRole = roleRepository.findByName("User");

            // Создаем администратора
            User writerUser = new User();
            writerUser.setActive(true);
            writerUser.setEmail("writer@gmail.com");
            String encodedPassword = passwordEncoder.encode("password"); // Зашифровать пароль
            writerUser.setPassword(encodedPassword);

            // Создаем коллекцию ролей для администратора
            Set<Role> roles = new HashSet<>();
            roles.add(writerRole);
            roles.add(userRole);

            // Устанавливаем роли для администратора
            writerUser.setRoles(roles);

            // Сохраняем администратора
            userRepository.save(writerUser);
        }
    }
    private void createDefaultAdmin() {
        // Проверяем, есть ли уже администратор в базе данных
        if (userRepository.count() == 0) {
            // Находим роли "Admin" и "User" в базе данных
            Role adminRole = roleRepository.findByName("Admin");
            Role writerRole = roleRepository.findByName("Writer");
            Role userRole = roleRepository.findByName("User");

            // Создаем администратора
            User adminUser = new User();
            adminUser.setActive(true);
            adminUser.setEmail("admin@gmail.com");
            String encodedPassword = passwordEncoder.encode("password"); // Зашифровать пароль
            adminUser.setPassword(encodedPassword);

            // Создаем коллекцию ролей для администратора
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(writerRole);
            roles.add(userRole);

            // Устанавливаем роли для администратора
            adminUser.setRoles(roles);

            // Сохраняем администратора
            userRepository.save(adminUser);
        }
    }
}
