package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.PublicationActivities;
import by.bntu.laboratory.models.PublicationActivitiesPage;
import by.bntu.laboratory.repo.PublicationActivitiesPageRepository;
import by.bntu.laboratory.repo.PublicationActivitiesRepository;
import by.bntu.laboratory.services.PublicationActivitiesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class PublicationsController {

    private final PublicationActivitiesService publicationActivitiesService;
    private final PublicationActivitiesRepository publicationActivitiesRepository;
    private final PublicationActivitiesPageRepository publicationActivitiesPageRepository;

    public PublicationsController(PublicationActivitiesService publicationActivitiesService,
                                  PublicationActivitiesRepository publicationActivitiesRepository,
                                  PublicationActivitiesPageRepository publicationActivitiesPageRepository) {

        this.publicationActivitiesService = publicationActivitiesService;
        this.publicationActivitiesRepository = publicationActivitiesRepository;
        this.publicationActivitiesPageRepository = publicationActivitiesPageRepository;
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer")
    public String writerPage() {
        return "users/writer/writer";
    }


    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/publication/add")
    public String PublicationAdd(Model model) {
        model.addAttribute("publication", new PublicationActivities());
        return "users/writer/add/publication-add";
    }

    @GetMapping("/publication/{publId}")
    public String redirectToFirstPage(@PathVariable Long publId) {
        Long firstPageId = publicationActivitiesService.getFirstPageId(publId);
        return "redirect:/publication/" + publId + "/" + firstPageId;
    }

    @GetMapping("/publication/{publId}/{pageId}")
    public String getPublicationPage(@PathVariable Long publId, @PathVariable Long pageId, Model model) {
        PublicationActivities publication = publicationActivitiesService.findById(publId);
        PublicationActivitiesPage page = publicationActivitiesService.findPageById(publId, pageId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!publication.getVisible() && !isWriter) {
            return "redirect:/";
        }
        if (!page.getVisible() && !isWriter) {
            return "redirect:/";
        }
        model.addAttribute("publication", publication);
        model.addAttribute("page", page);
        model.addAttribute("pages", publication.getPublicationActivitiesPages());
        return "views/publication-view";
    }

    /**
     * Method for adding news
     *
     * @param title Title of the news
     * @param cover Cover image for the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addPublication(@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam("cover") MultipartFile cover,
                                 @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                 Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }

            // Save the news article to the database
            PublicationActivities publicationActivities = new PublicationActivities();
            publicationActivities.setTitle(title);
            publicationActivities.setDescription(description);

            publicationActivitiesService.savePubActive(publicationActivities, cover);
            //  news.setCover(cover.getBytes());
            publicationActivities.setVisible(visible);
            //news.setVisible(true);

            publicationActivitiesRepository.save(publicationActivities);

            model.addAttribute("message", "Publication added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add publication: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/publication/edit/{publ_id}")
    public String publicationEdit(@PathVariable(value = "publ_id") long publId, Model model) {
        PublicationActivities publicationActivities = publicationActivitiesRepository.findById(publId)
                .orElseThrow(() -> new CustomNotFoundException("Publication not found with id " + publId));
        model.addAttribute("publication", publicationActivities);
        return "users/writer/edit/publication-edit";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication/edit/{publ_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String editPublication(
            @PathVariable(value = "publ_id") long publId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam("cover") MultipartFile cover,
            @RequestParam(value = "visible", defaultValue = "false") boolean visible,
            Model model) {
        try {

            // Save the news article to the database
            PublicationActivities publicationActivities = publicationActivitiesRepository.findByPublId(publId);
            publicationActivities.setTitle(title);
            publicationActivities.setDescription(description);
            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                publicationActivitiesService.savePubActiveImage(publicationActivities, cover);
            }

            //  news.setCover(cover.getBytes());
            publicationActivities.setVisible(visible);
            //news.setVisible(true);

            publicationActivitiesRepository.save(publicationActivities);

            model.addAttribute("message", "Publication saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save publication: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication/delete/{pub_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deletePublication(@PathVariable("pub_id") Long pubId) {
        PublicationActivities publicationActivities = publicationActivitiesRepository.findById(pubId).orElseThrow();
        publicationActivitiesRepository.delete(publicationActivities);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/publications")
    public String hiddenPublications(Model model) {
        List<PublicationActivities> publicationActivitiesList = publicationActivitiesRepository.findAll();

        model.addAttribute("publicationList", publicationActivitiesList);
        //model.addAttribute("emptyOrAllHidden", publicationActivitiesServices.isPubActiveListEmptyOrAllHidden(publicationActivitiesList));
        return "users/writer/hidden/publications-list";
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/publication-page/add")
    public String PublicationPageAdd(Model model) {
        List<PublicationActivities> publicationActivitiesList = publicationActivitiesRepository.findAll();
        model.addAttribute("publicationActivitiesList", publicationActivitiesList);
        model.addAttribute("publicationPage", new PublicationActivitiesPage());
        return "users/writer/add/publication-page-add";
    }

    /**
     * Method for adding news
     *
     * @param title Title of the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication-page/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addPublicationPage(@RequestParam Long publId,
                                     @RequestParam String title,
                                     @RequestParam String content,
                                     @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                     Model model) {
        try {
            PublicationActivities publicationActivities = publicationActivitiesRepository.findById(publId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid publication activities Id:" + publId));
            // Save the news article to the database
            PublicationActivitiesPage publicationActivitiesPage = new PublicationActivitiesPage();
            publicationActivitiesPage.setTitle(title);
            publicationActivitiesPage.setContent(content);
            publicationActivitiesPage.setPublicationActivities(publicationActivities);
            publicationActivitiesPage.setVisible(visible);

            publicationActivitiesPageRepository.save(publicationActivitiesPage);

            model.addAttribute("message", "Publication added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add publication: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/publication-page/edit/{page_id}")
    public String PublicationPageEdit(@PathVariable(value = "page_id") long pageId, Model model) {
        List<PublicationActivities> publicationActivitiesList = publicationActivitiesRepository.findAll();
        PublicationActivitiesPage publicationActivitiesPage = publicationActivitiesPageRepository.findById(pageId)
                .orElseThrow(() -> new CustomNotFoundException("Publication Page not found with id " + pageId));
        model.addAttribute("publicationActivitiesList", publicationActivitiesList);
        model.addAttribute("publicationPage", publicationActivitiesPage);
        return "users/writer/edit/publication-page-edit";
    }

    /**
     * Method for adding news
     *
     * @param title Title of the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication-page/edit/{page_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String editPublicationPage(@PathVariable(value = "page_id") Long pageId,
                                      @RequestParam long publId,
                                      @RequestParam String title,
                                      @RequestParam String content,
                                      @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                      Model model) {
        try {
            PublicationActivities publicationActivities = publicationActivitiesRepository.findById(publId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid publication activities page Id:" + publId));
            // Save the news article to the database
            PublicationActivitiesPage existingPublicationPage = publicationActivitiesPageRepository.findByPageId(pageId);
            existingPublicationPage.setTitle(title);
            existingPublicationPage.setContent(content);
            existingPublicationPage.setPublicationActivities(publicationActivities);
            existingPublicationPage.setVisible(visible);

            publicationActivitiesPageRepository.save(existingPublicationPage);

            model.addAttribute("message", "Publication page update successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update publication: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/publication-page/delete/{page_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deletePublicationPage(@PathVariable("page_id") Long pageId) {
        PublicationActivitiesPage publicationActivitiesPage = publicationActivitiesPageRepository.findById(pageId).orElseThrow();
        publicationActivitiesPageRepository.delete(publicationActivitiesPage);
        return "config/back";
    }
}
