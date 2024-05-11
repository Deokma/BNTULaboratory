package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.RegistrationCode;
import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import by.bntu.laboratory.repo.RoleRepository;
import by.bntu.laboratory.services.RegistrationCodeService;
import by.bntu.laboratory.services.RoleService;
import by.bntu.laboratory.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Controller with admin functions
 *
 * @author Denis Popolamov
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('Admin')")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final RegistrationCodeRepository registrationCodeRepository;
    private final RoleRepository roleRepository;

    /**
     * Go to the Admin panel page
     *
     * @return Admin Page
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("users", userService.list());
        return "users/admin/admin";
    }

    /**
     * Method for ban User
     */
    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/users/admin/admin";
    }

    @GetMapping("/admin/user/edit/{user}")
    public String userEdit(@PathVariable("user") User user, Model model) {
        model.addAttribute("user", user);
        List<Role> allRoles = roleService.findAllRoles();
        model.addAttribute("roles", allRoles);
        return "users/admin/user-edit";
    }

    @PostMapping("/admin/user/edit")
    public String userEdit(@RequestParam("userId") User user,
                           @RequestParam("roleIds") List<Long> roleIds,
                           @RequestParam Map<String, String> form) {
        List<Role> roles = roleService.findRolesByIds(roleIds);
        userService.changeUserRoles(user, new HashSet<>(roles));
        return "redirect:/admin";
    }

    /**
     * Method for unban User
     */
    @PostMapping("/admin/user/unban/{id}")
    public String userUnban(@PathVariable("id") Long id) {
        userService.unbanUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/codes")
    public String showGenerateCodesPage(Model model) {
        Role adminRole = roleRepository.findByName("Admin");
        Role writerRole = roleRepository.findByName("Writer");

        model.addAttribute("adminCodes", registrationCodeRepository.findByRole(adminRole));
        model.addAttribute("writerCodes", registrationCodeRepository.findByRole(writerRole));

        return "users/admin/codes";
    }


    @PostMapping("/admin/generate-admin-code")
    public String generateAdminCode() {
        generateAndSaveCodeForRole("Admin");
        return "redirect:/admin/codes";
    }

    @PostMapping("/admin/generate-writer-code")
    public String generateWriterCode() {
        generateAndSaveCodeForRole("Writer");
        return "redirect:/admin/codes";
    }


    private void generateAndSaveCodeForRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role != null) {
            // Генерируем новый код регистрации
            String code = generateUniqueCode();

            // Проверяем, что новый код уникален
            while (registrationCodeRepository.existsByCode(code)) {
                code = generateUniqueCode();
            }

            // Создаем объект регистрационного кода и сохраняем его в базе данных
            RegistrationCode registrationCode = new RegistrationCode();
            registrationCode.setCode(code);
            registrationCode.setRole(role);
            registrationCodeRepository.save(registrationCode);
        }
    }

    private String generateUniqueCode() {
        return RandomStringUtils.randomAlphanumeric(15);
    }

}