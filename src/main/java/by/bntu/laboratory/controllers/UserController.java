package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.RegistrationCode;
import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import by.bntu.laboratory.repo.RoleRepository;
import by.bntu.laboratory.repo.UserRepository;
import by.bntu.laboratory.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Denis Popolamov
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RegistrationCodeRepository registrationCodeRepository;

    /**
     * Go to registration page
     */
    @GetMapping("/registration")
    public String registrationPage() {
        return "authorization/registration";
    }

    /**
     * Go to login page
     */
    @GetMapping("/login")
    public String loginPage() {
        return "authorization/login";
    }

    /**
     * Method for create user
     *
     * @param user  User
     * @param model Model
     */
    @PostMapping("/registration")
    public String createUser(User user, String registrationCode, Model model) {
        // Извлекаем код регистрации из формы и ищем его в базе данных
        RegistrationCode code = registrationCodeRepository.findByCode(registrationCode);

        if (code == null) {
            // Если код регистрации не найден, возвращаем ошибку
            model.addAttribute("errorMessage", "Неправильный регистрационный код");
            return "authorization/registration";
        }

        if (!userService.createUser(user,code)) {
            model.addAttribute("errorMessage", "Пользователь с Email " + user.getEmail() + " Уже существует.");
            return "authorization/registration";
        }

        return "redirect:/login";
    }

    /**
     * Go to account page
     *
     * @param model     Model
     * @param principal The user who logged in
     */
    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Writer')")
    @GetMapping("/account")
    public String accountPage(Model model, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Collection<Role> roles = user.getRoles(); // Получаем роли пользователя
        model.addAttribute("roles", roles); // Добавляем роли в модель с именем "userRoles"

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Admin"));
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isWriter", isWriter);
        return "users/account";
    }
}