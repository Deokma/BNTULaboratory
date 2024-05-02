package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.*;
import by.bntu.laboratory.repo.*;
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
    private final EventsRepository eventsRepository;
    private final TimesRepository timesRepository;

    public PublicationsController(NewsRepository newsRepository, TagsRepository tagsRepository, ProjectsRepository projectsRepository,
                                  TagsService tagsService, NewsServices newsServices,
                                  TimesServices timesServices, EventsServices eventsServices, ProjectsServices projectsServices, EventsRepository eventsRepository, TimesRepository timesRepository) {
        this.newsRepository = newsRepository;
        this.tagsRepository = tagsRepository;
        this.projectsRepository = projectsRepository;
        this.tagsService = tagsService;
        this.newsServices = newsServices;
        this.timesServices = timesServices;
        this.eventsServices = eventsServices;
        this.projectsServices = projectsServices;
        this.eventsRepository = eventsRepository;
        this.timesRepository = timesRepository;
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
    @GetMapping("/news/add")
    public String newsAdd(Model model) {
        model.addAttribute("news", new News());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/news-add";
    }
    @GetMapping("/news")
    public String news(Model model) {
        List<News> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);
        return "views/news-list";
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
        return "views/news-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
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
     * Go to Add Event Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/event/add")
    public String eventAdd(Model model) {
        model.addAttribute("event", new EventsCalendar());
        model.addAttribute("tags", tagsRepository.findAll());
        return "users/writer/event-add";
    }
    @GetMapping("/events")
    public String events(Model model) {
        List<EventsCalendar> eventsCalendars = eventsRepository.findAll();
        model.addAttribute("eventCalendarList", eventsCalendars);
        return "views/event-list";
    }
    /**
     * Go to Events Page
     */
    @GetMapping("/event/{eventId}")
    public String viewEventByTitle(@PathVariable("eventId") Long eventId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<EventsCalendar> events = eventsServices.findById(eventId); // Предполагается, что у вас есть сервис для работы с новостями
        if (events.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error-page"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        model.addAttribute("events", events);
        return "views/event-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
    }
    /**
     * Method for adding event
     *
     * @param title   Title of the event
     * @param content Content of the event
     * @param cover   Cover image for the event
     * @param tags    Tags for the event
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/event/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addEvent(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam("cover") MultipartFile cover,
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
            EventsCalendar eventsCalendar = new EventsCalendar();
            eventsCalendar.setTitle(title);
            eventsCalendar.setContent(content);
            eventsCalendar.setTags(savedTags);
            eventsCalendar.setVisible(true);
            eventsCalendar.setDate(new Date());
            eventsServices.saveEvent(eventsCalendar, cover);
            //  news.setCover(cover.getBytes());


            eventsRepository.save(eventsCalendar);

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
    public String projectAdd(Model model) {
        model.addAttribute("projects", new Projects());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/project-add";
    }
    @GetMapping("/projects")
    public String project(Model model) {
        List<Projects> projects = projectsRepository.findAll();
        model.addAttribute("projectsList", projects);
        return "views/project-list";
    }
    /**
     * Go to News Page
     */
    @GetMapping("/project/{projectId}")
    public String viewProjectByTitle(@PathVariable("projectId") Long projectId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<Projects> projects = projectsServices.findById(projectId); // Предполагается, что у вас есть сервис для работы с новостями
        if (projects.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error-page"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        model.addAttribute("project", projects);
        return "views/project-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
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
            projects.setContent(content);
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

    /**
     * Go to Add Event Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/times/add")
    public String timesAdd(Model model) {
        model.addAttribute("times", new TimesReviews());
        model.addAttribute("tags", tagsRepository.findAll());
        return "users/writer/times-add";
    }
    @GetMapping("/times")
    public String times(Model model) {
        List<TimesReviews> timesReviews = timesRepository.findAll();
        model.addAttribute("timesList", timesReviews);
        return "views/times-list";
    }
    /**
     * Go to Events Page
     */
    @GetMapping("/times/{timesId}")
    public String viewTimesByTitle(@PathVariable("timesId") Long timesId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<TimesReviews> timesReviews = timesServices.findById(timesId); // Предполагается, что у вас есть сервис для работы с новостями
        if (timesReviews.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        model.addAttribute("times", timesReviews);
        return "views/times-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
    }
    /**
     * Method for adding event
     *
     * @param title   Title of the event
     * @param content Content of the event
     * @param cover   Cover image for the event
     * @param tags    Tags for the event
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/times/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addTimes(@RequestParam String title,
                           @RequestParam String content,
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
            timesReviews.setContent(content);
            timesReviews.setTags(savedTags);
            timesReviews.setVisible(true);
            timesReviews.setDate(new Date());
            timesReviews.setLink(link);
            timesServices.saveTimes(timesReviews, cover);
            //  news.setCover(cover.getBytes());


            timesRepository.save(timesReviews);

            model.addAttribute("message", "News added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "redirect:/";
    }

}
