package by.bntu.laboratory.services;

import by.bntu.laboratory.models.RegistrationCode;
import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import by.bntu.laboratory.repo.RoleRepository;
import by.bntu.laboratory.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Denis Popolamov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final RegistrationCodeRepository registrationCodeRepository;
@Transactional
    public boolean createUser(User user, RegistrationCode code) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        // Создаем объект Role и сохраняем его
        Role userRole = roleRepository.findByName("User");
        if (userRole == null) {
            userRole = new Role("User");
            roleRepository.save(userRole);
        }

        // Устанавливаем роль пользователю
        //user.setRoles(Collections.singleton(userRole));
        changeUserRoles(user, new HashSet<>(Collections.singleton(code.getRole())));
        registrationCodeRepository.deleteRegistrationCodeByCode(code.getCode());
        log.info("Saving new User with username: " + user.getUsername());
        userRepository.save(user);
        return true;
    }


    public List<User> list() {
        return (List<User>) userRepository.findAll();
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(false);
            log.info("Ban user with id = {}; name: {}", user.getId(), user.getUsername());
        }
        userRepository.save(user);
    }

    public void unbanUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(true);
            log.info("Unban user with id = {}; name: {}", user.getId(), user.getUsername());
        }
        userRepository.save(user);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    public void changeUserRoles(User user, Set<Role> roles) {
        user.setRoles(roles);
        userRepository.save(user);
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // далее, можно использовать полученное имя пользователя
        // для поиска соответствующего объекта User в базе данных
        // и вернуть его из метода
        // например, используя UserRepository
        return userRepository.findByEmail(username);
    }
}