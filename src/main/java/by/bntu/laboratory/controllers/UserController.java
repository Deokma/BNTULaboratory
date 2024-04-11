package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.UserRepository;
import by.bntu.laboratory.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Collection;

/**
 * @author Denis Popolamov
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
//    @Autowired
//    private final AvatarImageRepository avatarRepository;
    // private final AvatarImageService avatarService;

    /**
     * Go to registration page
     */
    @GetMapping("/registration")
    public String registrationPage() {
        return "registration";
    }

    /**
     * Go to login page
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Method for create user
     *
     * @param user  User
     * @param model Model
     */
    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "User with Username " + user.getUsername() + " already exist.");
            return "registration";
        }
        userService.createUser(user);
        return "redirect:/login";
    }

    /**
     * Go to account page
     *
     * @param model     Model
     * @param principal The user who logged in
     */
    @PreAuthorize("hasAuthority('User')")
    @GetMapping("/account")
    public String accountPage(Model model, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Collection<Role> roles = user.getRoles(); // Получаем роли пользователя
        model.addAttribute("roles", roles); // Добавляем роли в модель с именем "userRoles"
        return "account";
    }


    /**
     * Go to another user page
     *
     * @param user      User
     * @param model     Model
     * @param principal The user who logged in
     */
    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        User user_principal = userService.getUserByPrincipal(principal);
        if (user_principal == user) {
            return "redirect:/account";
        }
        // model.addAttribute("book_list", user.getBooks_list());
        return "user-info";
    }

//    @PostMapping("/upload_avatar/{user_id}")
//    public String uploadImage(@PathVariable("user_id") Long user_id, @RequestParam("avatar") MultipartFile avatar, Model model) {
//        try {
//            AvatarImage checkAvatarImage = avatarService.findById(user_id);
//            if (avatar.isEmpty()) {
//                model.addAttribute("error", "Please select a file to upload");
//                return "/account";
//            }
//            if (checkAvatarImage.getUser() == null) {
//                AvatarImage avatarImage = new AvatarImage();
//                avatarImage.setFileName(avatar.getOriginalFilename());
//                avatarImage.setData(avatar.getBytes());
//               // avatarImage.setUser(userService.getCurrentUser());
//                avatarRepository.save(avatarImage);
//                return "/account";
//            } else {
//                checkAvatarImage.setFileName(avatar.getOriginalFilename());
//                checkAvatarImage.setData(avatar.getBytes());
//               // checkAvatarImage.setUser(userService.getCurrentUser());
//                avatarRepository.save(checkAvatarImage);
//                return "/account";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "error";
//    }

//    @GetMapping("/avatarImage/{id}")
//    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException, ChangeSetPersister.NotFoundException {
//        AvatarImage avatar = avatarRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
//
//        byte[] image = avatar.getData();
//        final HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG);
//        return new ResponseEntity<>(image, headers, HttpStatus.OK);
//    }
}