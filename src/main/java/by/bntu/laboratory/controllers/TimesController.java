package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.repo.TimesRepository;
import by.bntu.laboratory.services.TagsService;
import by.bntu.laboratory.services.TimesService;
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class TimesController {
    private final TagsRepository tagsRepository;
    private final TagsService tagsService;
    private final TimesService timesService;
    private final TimesRepository timesRepository;

    public TimesController(TagsRepository tagsRepository, TagsService tagsService, TimesService timesService,
                           TimesRepository timesRepository) {
        this.tagsRepository = tagsRepository;
        this.tagsService = tagsService;
        this.timesService = timesService;
        this.timesRepository = timesRepository;
    }

    /**
     * Go to Add Event Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/times/add")
    public String timesAdd(Model model) {
        model.addAttribute("times", new TimesReviews());
        model.addAttribute("tags", tagsRepository.findAll());
        return "users/writer/add/times-add";
    }

    @GetMapping("/times")
    public String times(Model model) {
        List<TimesReviews> timesReviews = timesRepository.findAll();
        model.addAttribute("timesList", timesReviews);
        model.addAttribute("emptyOrAllHidden", timesService.isTimesListEmptyOrAllHidden(timesReviews));

        return "views/lists/times-list";
    }

    /**
     * Go to Events Page
     */
    @GetMapping("/times/{timesId}")
    public String viewTimes(@PathVariable("timesId") Long timesId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<TimesReviews> timesReviewsOptional = timesService.findById(timesId); // Предполагается, что у вас есть сервис для работы с новостями
        if (timesReviewsOptional.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }

        TimesReviews timesReviews = timesReviewsOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!timesReviews.getVisible() && !isWriter) {
            return "redirect:/";
        }
        model.addAttribute("times", timesReviews);
        return "redirect:" + timesReviews.getLink(); // Предполагается, что у вас есть шаблон для отображения одной новости
    }

    /**
     * Method for adding event
     *
     * @param title Title of the event
     * @param cover Cover image for the event
     * @param tags  Tags for the event
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/times/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addTimes(@RequestParam String title,
                           @RequestParam("cover") MultipartFile cover,
                           @RequestParam("link") String link,
                           @RequestParam(value = "tags", required = false) String tags,
                           Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
            }

            // Save the news article to the database
            TimesReviews timesReviews = new TimesReviews();
            timesReviews.setTitle(title);
            // timesReviews.setContent(content);
            timesReviews.setTags(savedTags);
            timesReviews.setVisible(true);
            timesReviews.setDate(new Date());
            timesReviews.setLink(link);
            timesService.saveTimes(timesReviews, cover);
            //  news.setCover(cover.getBytes());


            timesRepository.save(timesReviews);

            model.addAttribute("message", "Times added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add times: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/times/edit/{times_id}")
    public String timesEdit(@PathVariable(value = "times_id") long timesId, Model model) {
        TimesReviews times = timesRepository.findById(timesId)
                .orElseThrow(() -> new CustomNotFoundException("Times not found with id " + timesId));
        Collection<Tags> tags = times.getTags();
        model.addAttribute("times", times);
        model.addAttribute("tags", tags);
        return "users/writer/edit/times-edit";
    }

    /**
     * Method for adding news
     *
     * @param title Title of the news
     * @param cover Cover image for the news
     * @param tags  Tags for the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/times/edit/{times_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String timesEditPost(@PathVariable(value = "times_id") long timesId,
                                @RequestParam String title,
                                @RequestParam("cover") MultipartFile cover,
                                @RequestParam String link,
                                @RequestParam(value = "tags", required = false) String tags,
                                @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                Model model) {
        try {
            // Validate file type for cover image

            TimesReviews existingTimes = timesRepository.findByTimesId(timesId);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingTimes.setTags(savedTags);
            }

            // Save the news article to the database
            existingTimes.setTitle(title);


            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                timesService.saveTimesImage(existingTimes, cover);
            }
            existingTimes.setLink(link);
            existingTimes.setVisible(visible);
            timesRepository.save(existingTimes);

            model.addAttribute("message", "Times edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit times: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/times/delete/{times_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteTimes(@PathVariable("times_id") Long timesId) {
        TimesReviews timesReviews = timesRepository.findById(timesId).orElseThrow();
        timesRepository.delete(timesReviews);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/times")
    public String hiddenTimes(Model model) {
        List<TimesReviews> timesReviewsList = timesRepository.findAll();
        model.addAttribute("timesReviewsList", timesReviewsList);
        model.addAttribute("emptyOrAllHidden", timesService.isTimesListEmptyOrAllHidden(timesReviewsList));
        return "users/writer/hidden/times-list";
    }
}
