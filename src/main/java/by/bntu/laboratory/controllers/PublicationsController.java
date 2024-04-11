package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.ProjectsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class PublicationsController {

    private final NewsRepository newsRepository;
    private final TagsRepository tagsRepository;
    private final ProjectsRepository projectsRepository;
    private final TagsService tagsService;
    private final NewsServices newsServices;
    private final TimesServices timesServices;
    private final EventsServices eventsServices;
    private final ProjectsServices projectsServices;

    public PublicationsController(NewsRepository newsRepository, TagsRepository tagsRepository, ProjectsRepository projectsRepository,
                                  TagsService tagsService, NewsServices newsServices,
                                  TimesServices timesServices, EventsServices eventsServices, ProjectsServices projectsServices) {
        this.newsRepository = newsRepository;
        this.tagsRepository = tagsRepository;
        this.projectsRepository = projectsRepository;
        this.tagsService = tagsService;
        this.newsServices = newsServices;
        this.timesServices = timesServices;
        this.eventsServices = eventsServices;
        this.projectsServices = projectsServices;
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/news/add")
    public String newsAdd(Model model) {
        model.addAttribute("news", new News());
        return "news-add";
    }
    /**
     * Go to News Page
     */
    @GetMapping("/news/{newsId}")
    public String viewNewsByTitle(@PathVariable("newsId") Long newsId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<News> news = newsServices.findById(newsId); // Предполагается, что у вас есть сервис для работы с новостями
        if (news.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error-page"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        model.addAttribute("news", news);
        return "news-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
    }

    /**
     * Method for adding news
     *
     * @param title   Title of the news
     * @param content Content of the news
     * @param cover   Cover image for the news
     * @param tags    Tags for the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/news/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addNews(@RequestParam String title,
                          @RequestParam String content,
                          @RequestParam String description,
                          @RequestParam("cover") MultipartFile cover,
                          @RequestParam(value = "tags", required = false) String tags,
                          Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }
            // Set the current date
            Date currentDate = new Date();
            // Parse and save tags if provided
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
            }

            // Save the news article to the database
            News news = new News();
            news.setTitle(title);
            news.setContent(content);
            news.setDescription(description);
            news.setDate(currentDate);
            news.setTags(savedTags);
            newsServices.saveNews(news, cover);
            //  news.setCover(cover.getBytes());
            news.setVisible(true);

            newsRepository.save(news);

            model.addAttribute("message", "News added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "redirect:/";
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/project/add")
    public String projectAdd() {
        return "project-add";
    }

    /**
     * Method for adding project
     *
     * @param title   Title of the project
     * @param content Content of the project
     * @param cover   Cover image for the project
     * @param tags    Tags for the project
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/project/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addProject(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam("cover") MultipartFile cover,
                             @RequestParam(value = "tags", required = false) String tags,
                             Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }
            // Set the current date
            //Date currentDate = new Date();
            // Parse and save tags if provided
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
            }

            // Save the news article to the database
            Projects projects = new Projects();
            projects.setTitle(title);
            projects.setTags(savedTags);
            projects.setVisible(true);
            projectsServices.saveProjects(projects, cover);
            //  news.setCover(cover.getBytes());


            projectsRepository.save(projects);

            model.addAttribute("message", "News added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "redirect:/";
    }
}
