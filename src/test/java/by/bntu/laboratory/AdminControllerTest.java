package by.bntu.laboratory;

import by.bntu.laboratory.controllers.AdminController;
import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import by.bntu.laboratory.repo.RoleRepository;
import by.bntu.laboratory.services.RoleService;
import by.bntu.laboratory.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private RegistrationCodeRepository registrationCodeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка замоканного SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void admin_ShouldReturnAdminPage() {
        List<User> users = Collections.singletonList(new User());
        when(userService.list()).thenReturn(users);

        String result = adminController.admin(model);

        verify(model, times(1)).addAttribute("users", users);
        assertEquals("users/admin/admin", result);
    }

    @Test
    void userBan_ShouldRedirectToAdminPage() {
        Long userId = 1L;

        String result = adminController.userBan(userId);

        verify(userService, times(1)).banUser(userId);
        assertEquals("redirect:/users/admin/admin", result);
    }

    @Test
    void userEdit_ShouldReturnUserEditPage() {
        User user = new User();
        List<Role> roles = Collections.singletonList(new Role());
        when(userService.findById(anyLong())).thenReturn(user);
        when(roleService.findAllRoles()).thenReturn(roles);

        String result = adminController.userEdit(user, model);

        verify(model, times(1)).addAttribute("user", user);
        verify(model, times(1)).addAttribute("roles", roles);
        assertEquals("users/admin/user-edit", result);
    }

    @Test
    void userEditPost_ShouldRedirectToAdminPage() {
        User user = new User();
        List<Long> roleIds = Collections.singletonList(1L);
        List<Role> roles = Collections.singletonList(new Role());
        when(roleService.findRolesByIds(roleIds)).thenReturn(roles);

        String result = adminController.userEdit(user, roleIds, Collections.emptyMap());

        verify(userService, times(1)).changeUserRoles(user, new HashSet<>(roles));
        assertEquals("redirect:/admin", result);
    }

    @Test
    void userUnban_ShouldRedirectToAdminPage() {
        Long userId = 1L;

        String result = adminController.userUnban(userId);

        verify(userService, times(1)).unbanUser(userId);
        assertEquals("redirect:/admin", result);
    }

    // Дополнительные тесты для методов генерации кодов и других методов можно добавить аналогичным образом
}
