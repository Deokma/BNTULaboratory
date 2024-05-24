package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.User;
import by.bntu.laboratory.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

/**
 * @author Denis Popolamov
 */
@ControllerAdvice
@RequiredArgsConstructor
public class EverewhereController {

    private final UserService userService;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @ModelAttribute
    public void addCsrfToken(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, null);
        }
        model.addAttribute("_csrf", csrfToken);
    }
    /**
     * The method adds the logged-in user
     * attribute to all page
     *
     * @param model     Model
     * @param principal The user who logged in
     */
    @ModelAttribute
    public void addModelInformation(Model model, Principal principal) {
        User user_session = userService.getUserByPrincipal(principal);
        model.addAttribute("usersession", user_session);
    }

    @GetMapping("/back")
    public String back() {
        return "redirect:/back";
    }



}