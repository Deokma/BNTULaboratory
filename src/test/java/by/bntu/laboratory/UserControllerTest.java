package by.bntu.laboratory;

import by.bntu.laboratory.controllers.UserController;
import by.bntu.laboratory.models.RegistrationCode;
import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import by.bntu.laboratory.repo.UserRepository;
import by.bntu.laboratory.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationCodeRepository registrationCodeRepository;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testRegistrationPage() {
        String viewName = userController.registrationPage();
        assertEquals("authorization/registration", viewName);
    }

    @Test
    public void testLoginPage() {
        String viewName = userController.loginPage();
        assertEquals("authorization/login", viewName);
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User();
        String registrationCode = "validCode";
        RegistrationCode code = new RegistrationCode();
        when(registrationCodeRepository.findByCode(registrationCode)).thenReturn(code);
        when(userService.createUser(user, code)).thenReturn(true);

        String viewName = userController.createUser(user, registrationCode, model);

        assertEquals("redirect:/login", viewName);
        verify(model, never()).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    public void testCreateUser_InvalidRegistrationCode() {
        User user = new User();
        String registrationCode = "invalidCode";
        when(registrationCodeRepository.findByCode(registrationCode)).thenReturn(null);

        String viewName = userController.createUser(user, registrationCode, model);

        assertEquals("authorization/registration", viewName);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Неправильный регистрационный код"));
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");
        String registrationCode = "validCode";
        RegistrationCode code = new RegistrationCode();
        when(registrationCodeRepository.findByCode(registrationCode)).thenReturn(code);
        when(userService.createUser(user, code)).thenReturn(false);

        String viewName = userController.createUser(user, registrationCode, model);

        assertEquals("authorization/registration", viewName);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Пользователь с Email test@example.com Уже существует."));
    }

   /* @Test
    public void testAccountPage() {
        User user = new User();
        user.setRoles(Collections.singleton(new Role("Writer")));
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        when(authentication.getAuthorities()).thenReturn(Collections.<GrantedAuthority>singletonList(() -> "Writer"));

        String viewName = userController.accountPage(model, principal);

        assertEquals("users/account", viewName);
        verify(model, times(1)).addAttribute("roles", user.getRoles());
        verify(model, times(1)).addAttribute("isAdmin", false);
        verify(model, times(1)).addAttribute("isWriter", true);
    }*/
}
