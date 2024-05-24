package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.OnlineServices;
import by.bntu.laboratory.repo.OnlineServicesRepository;
import by.bntu.laboratory.services.OnlineServicesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class OnlineServicesController {

    private final OnlineServicesRepository onlineServicesRepository;
    private final OnlineServicesService onlineServicesService;

    public OnlineServicesController(OnlineServicesRepository onlineServicesRepository, OnlineServicesService onlineServicesService) {
        this.onlineServicesRepository = onlineServicesRepository;
        this.onlineServicesService = onlineServicesService;
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/online-service/add")
    public String OnlineServicesAdd(Model model) {
        model.addAttribute("oss", new OnlineServices());
        return "users/writer/add/online-service-add";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/online-service/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addOnlineService(@RequestParam String title,
                                   @RequestParam String link,
                                   @RequestParam String cover,
                                   @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                   Model model) {
        try {
            // Удаление всех атрибутов fill="" из строки SVG
            String sanitizedCover = cover.replaceAll("fill=\"[^\"]*\"", "");

            // Save the os article to the online service
            OnlineServices onlineServices = new OnlineServices();
            onlineServices.setTitle(title);
            onlineServices.setLink(link);
            onlineServices.setCover(sanitizedCover);
            onlineServices.setVisible(visible);

            onlineServicesRepository.save(onlineServices);

            model.addAttribute("message", "Online Service added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add Online Service: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }


    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/online-services")
    public String hiddenOnlineServices(Model model) {
        List<OnlineServices> onlineServicesList = onlineServicesRepository.findAll();
        model.addAttribute("onlineServicesList", onlineServicesList);
        model.addAttribute("emptyOrAllHidden", onlineServicesService.isOnlineServicesListEmptyOrAllHidden(onlineServicesList));
        return "users/writer/hidden/online-services-list";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/online-service/edit/{os_id}")
    public String onlineServicesEdit(@PathVariable(value = "os_id") long osId, Model model) {
        OnlineServices onlineServices = onlineServicesRepository.findById(osId)
                .orElseThrow(() -> new CustomNotFoundException("Online Service not found with id " + osId));
        model.addAttribute("onlineService", onlineServices);
        //model.addAttribute("tags", tags);
        return "users/writer/edit/online-service-edit";
    }

    /**
     * Method for adding news
     *
     * @param title Title of the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/online-service/edit/{os_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String onlineServicesEditPost(@PathVariable(value = "os_id") long osId,
                                         @RequestParam String title,
                                         @RequestParam String cover,
                                         @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                         Model model) {
        try {
            // Validate file type for cover image

            OnlineServices existingOnlineServices = onlineServicesRepository.findOnlineServicesByOsID(osId);
            // Удаление всех атрибутов fill="" из строки SVG
            String sanitizedCover = cover.replaceAll("fill=\"[^\"]*\"", "");

            // Save the news article to the database
            existingOnlineServices.setTitle(title);
            existingOnlineServices.setCover(sanitizedCover);
            existingOnlineServices.setVisible(visible);

            onlineServicesRepository.save(existingOnlineServices);

            model.addAttribute("message", "DataBase edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit dataBase: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/online-service/delete/{os_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteOnlineServices(@PathVariable("os_id") Long osId) {
        OnlineServices onlineServices = onlineServicesRepository.findById(osId).orElseThrow();
        onlineServicesRepository.delete(onlineServices);
        return "config/back";
    }

}
