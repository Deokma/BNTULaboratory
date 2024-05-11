package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.*;
import by.bntu.laboratory.repo.*;
import by.bntu.laboratory.services.*;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final DataBaseRepository dataBaseRepository;
    private final DataBasesServices dataBasesServices;
    private final OnlineServicesRepository onlineServicesRepository;
    private final OnlineServicesServices onlineServicesServices;
    private final PublicationActivitiesServices publicationActivitiesServices;
    private final PublicationActivitiesRepository publicationActivitiesRepository;
    private final PublicationActivitiesPageRepository publicationActivitiesPageRepository;

    public PublicationsController(NewsRepository newsRepository, TagsRepository tagsRepository,
                                  ProjectsRepository projectsRepository,
                                  TagsService tagsService, NewsServices newsServices,
                                  TimesServices timesServices, EventsServices eventsServices,
                                  ProjectsServices projectsServices, EventsRepository eventsRepository,
                                  TimesRepository timesRepository, DataBaseRepository dataBaseRepository, DataBasesServices dataBasesServices, OnlineServicesRepository onlineServicesRepository, OnlineServicesServices onlineServicesServices, PublicationActivitiesServices publicationActivitiesServices, PublicationActivitiesRepository publicationActivitiesRepository, PublicationActivitiesPageRepository publicationActivitiesPageRepository) {
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
        this.dataBaseRepository = dataBaseRepository;
        this.dataBasesServices = dataBasesServices;
        this.onlineServicesRepository = onlineServicesRepository;
        this.onlineServicesServices = onlineServicesServices;
        this.publicationActivitiesServices = publicationActivitiesServices;
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
    @GetMapping("/writer/news/add")
    public String newsAdd(Model model) {
        model.addAttribute("news", new News());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/add/news-add";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/news/edit/{news_id}")
    public String newsEdit(@PathVariable(value = "news_id") long news_id, Model model) {
        News news = newsRepository.findById(news_id)
                .orElseThrow(() -> new CustomNotFoundException("News not found with id " + news_id));
        Collection<Tags> newsTags = news.getTags();
        List<Long> newsTagIds = newsTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("news", news);
        model.addAttribute("newsTags", newsTagIds);
        model.addAttribute("tags", tags);
        return "users/writer/edit/news-edit";
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
    @PostMapping(value = "/writer/news/edit/{news_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String newsEditPost(@PathVariable(value = "news_id") long news_id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam String description,
                               @RequestParam("cover") MultipartFile cover,
                               @RequestParam(value = "tags", required = false) String tags,
                               @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                               Model model) {
        try {
            // Validate file type for cover image

            News existingNews = newsRepository.findByNewsId(news_id);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingNews.setTags(savedTags);
            }

            // Save the news article to the database
            existingNews.setTitle(title);
            existingNews.setContent(content);
            existingNews.setDescription(description);

            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                newsServices.saveNewsImage(existingNews, cover);
            }
            existingNews.setVisible(visible);
            newsRepository.save(existingNews);

            model.addAttribute("message", "News edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/news/delete/{news_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteNews(@PathVariable("news_id") Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow();
        newsRepository.delete(news);
        return "redirect:config/back";
    }

    @GetMapping("/news")
    public String news(Model model) {
        List<News> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);
        model.addAttribute("emptyOrAllHidden", newsServices.isNewsListEmptyOrAllHidden(newsList));
        return "views/lists/news-list";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/news")
    public String hiddenNews(Model model) {
        List<News> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);
        model.addAttribute("emptyOrAllHidden", newsServices.isNewsListEmptyOrAllHidden(newsList));
        return "users/writer/hidden/news-list";
    }

    /**
     * Go to News Page
     */
    @GetMapping("/news/{newsId}")
    public String viewNews(@PathVariable("newsId") Long newsId, Model model, Principal principal) {
        Optional<News> newsOptional = newsServices.findById(newsId); // Получение новости по ID
        if (newsOptional.isEmpty()) {
            return "error"; // Вернуть страницу с ошибкой
        }

        News news = newsOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!news.getVisible() && !isWriter) {
            return "redirect:/";
        }

        model.addAttribute("news", news);
        return "views/news-view"; // Вернуть шаблон для отображения новости
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
    @PostMapping(value = "/writer/news/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addNews(@RequestParam String title,
                          @RequestParam String content,
                          @RequestParam String description,
                          @RequestParam("cover") MultipartFile cover,
                          @RequestParam(value = "tags", required = false) String tags,
                          @RequestParam(value = "visible", defaultValue = "false") boolean visible,
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
            news.setVisible(visible);
            //news.setVisible(true);

            newsRepository.save(news);

            model.addAttribute("message", "News added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }


    @GetMapping("/events")
    public String events(Model model) {
        List<EventsCalendar> eventsCalendars = eventsRepository.findAll();
        model.addAttribute("eventCalendarList", eventsCalendars);
        model.addAttribute("emptyOrAllHidden", eventsServices.isEventsListEmptyOrAllHidden(eventsCalendars));

        return "views/lists/event-list";
    }

    /**
     * Go to Add Event Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/event/add")
    public String eventAdd(Model model) {
        model.addAttribute("event", new EventsCalendar());
        model.addAttribute("tags", tagsRepository.findAll());
        return "users/writer/add/event-add";
    }

    /**
     * Go to Events Page
     */
    @GetMapping("/event/{eventId}")
    public String viewEvent(@PathVariable("eventId") Long eventId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<EventsCalendar> events = eventsServices.findById(eventId); // Предполагается, что у вас есть сервис для работы с новостями
        if (events.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
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
    @PostMapping(value = "/writer/event/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addEvent(@RequestParam String title,
                           @RequestParam String content,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date date,
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
            eventsCalendar.setDate(date);
            eventsServices.saveEvent(eventsCalendar, cover);

            eventsRepository.save(eventsCalendar);

            model.addAttribute("message", "Event added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add event: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/event/edit/{event_id}")
    public String eventEdit(@PathVariable(value = "event_id") long eventId, Model model) {
        EventsCalendar eventsCalendar = eventsRepository.findById(eventId)
                .orElseThrow(() -> new CustomNotFoundException("Event not found with id " + eventId));
        Collection<Tags> eventTags = eventsCalendar.getTags();
        List<Long> eventTagIds = eventTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("event", eventsCalendar);
        model.addAttribute("eventTags", eventTagIds);
        model.addAttribute("tags", tags);
        return "users/writer/edit/event-calendar-edit";
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
    @PostMapping(value = "/writer/event/edit/{event_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String eventEditPost(@PathVariable(value = "event_id") long eventId,
                                @RequestParam String title,
                                @RequestParam String content,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date date,
                                @RequestParam("cover") MultipartFile cover,
                                @RequestParam(value = "tags", required = false) String tags,
                                @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                Model model) {
        try {
            // Validate file type for cover image

            EventsCalendar existingEvent = eventsRepository.findEventsCalendarByEventId(eventId);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingEvent.setTags(savedTags);
            }

            // Save the news article to the database
            existingEvent.setTitle(title);
            existingEvent.setContent(content);
            existingEvent.setDate(date);
            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                eventsServices.saveEventImage(existingEvent, cover);
            }
            existingEvent.setVisible(visible);
            eventsRepository.save(existingEvent);

            model.addAttribute("message", "Event edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit event: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/event/delete/{event_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteEvent(@PathVariable("event_id") Long eventId) {
        EventsCalendar eventsCalendar = eventsRepository.findById(eventId).orElseThrow();
        eventsRepository.delete(eventsCalendar);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/events")
    public String hiddenEvent(Model model) {
        List<EventsCalendar> eventsCalendarList = eventsRepository.findAll();
        model.addAttribute("eventsCalendarList", eventsCalendarList);
        model.addAttribute("emptyOrAllHidden", eventsServices.isEventsListEmptyOrAllHidden(eventsCalendarList));
        return "users/writer/hidden/events-list";
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/project/add")
    public String projectAdd(Model model) {
        model.addAttribute("projects", new Projects());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/add/project-add";
    }

    @GetMapping("/projects")
    public String project(Model model) {
        List<Projects> projects = projectsRepository.findAll();
        model.addAttribute("projectsList", projects);
        model.addAttribute("emptyOrAllHidden", projectsServices.isProjectListEmptyOrAllHidden(projects));

        return "views/lists/project-list";
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
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
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
    @PostMapping(value = "/writer/project/add", consumes = MULTIPART_FORM_DATA_VALUE)
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

            model.addAttribute("message", "Project added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add project: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/project/edit/{project_id}")
    public String projectsEdit(@PathVariable(value = "project_id") long projectId, Model model) {
        Projects projects = projectsRepository.findById(projectId)
                .orElseThrow(() -> new CustomNotFoundException("Project not found with id " + projectId));
        Collection<Tags> projectTags = projects.getTags();
        List<Long> projectsTagIds = projectTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("projectsTags", projectsTagIds);
        model.addAttribute("tags", tags);
        return "users/writer/edit/project-edit";
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
    @PostMapping(value = "/writer/project/edit/{project_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String projectsEditPost(@PathVariable(value = "project_id") Long projectId,
                                   @RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam("cover") MultipartFile cover,
                                   @RequestParam(value = "tags", required = false) String tags,
                                   @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                   Model model) {
        try {
            // Validate file type for cover image

            Projects existingProject = projectsRepository.findByProjectId(projectId);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingProject.setTags(savedTags);
            }

            // Save the news article to the database
            existingProject.setTitle(title);
            existingProject.setContent(content);

            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                projectsServices.saveProjectImage(existingProject, cover);
            }
            existingProject.setVisible(visible);
            projectsRepository.save(existingProject);

            model.addAttribute("message", "Project edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit project: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/project/delete/{project_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteProject(@PathVariable("project_id") Long projectId) {
        Projects projects = projectsRepository.findById(projectId).orElseThrow();
        projectsRepository.delete(projects);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/projects")
    public String hiddenProjects(Model model) {
        List<Projects> projectsList = projectsRepository.findAll();
        model.addAttribute("projectsList", projectsList);
        model.addAttribute("emptyOrAllHidden", projectsServices.isProjectListEmptyOrAllHidden(projectsList));
        return "users/writer/hidden/projects-list";
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
        model.addAttribute("emptyOrAllHidden", timesServices.isTimesListEmptyOrAllHidden(timesReviews));

        return "views/lists/times-list";
    }

    /**
     * Go to Events Page
     */
    @GetMapping("/times/{timesId}")
    public String viewTimes(@PathVariable("timesId") Long timesId, Model model) {
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
            timesServices.saveTimes(timesReviews, cover);
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
                timesServices.saveTimesImage(existingTimes, cover);
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
        model.addAttribute("emptyOrAllHidden", timesServices.isTimesListEmptyOrAllHidden(timesReviewsList));
        return "users/writer/hidden/times-list";
    }

    @GetMapping("/data-bases")
    public String dataBases(Model model) {
        List<DataBases> dataBasesList = dataBaseRepository.findAll();
        model.addAttribute("dataBasesList", dataBasesList);
        model.addAttribute("emptyOrAllHidden", dataBasesServices.isDataBaseListEmptyOrAllHidden(dataBasesList));
        return "views/lists/data-bases-list";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/data-base/add")
    public String dataBasesAdd(Model model) {
        model.addAttribute("dbs", new DataBases());
        return "users/writer/add/data-bases-add";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addDataBase(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String content,
            /*     @RequestParam(value = "tags", required = false) String tags,*/
                              @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                              Model model) {
        try {
            // Save the news article to the database
            DataBases dataBases = new DataBases();
            dataBases.setTitle(title);
            dataBases.setDescription(description);
            dataBases.setContent(content);
            dataBases.setVisible(visible);

            dataBaseRepository.save(dataBases);

            model.addAttribute("message", "DB added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add db: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/data-bases")
    public String hiddenDataBases(Model model) {
        List<DataBases> dataBasesList = dataBaseRepository.findAll();
        model.addAttribute("dataBasesList", dataBasesList);
        model.addAttribute("emptyOrAllHidden", dataBasesServices.isDataBaseListEmptyOrAllHidden(dataBasesList));
        return "users/writer/hidden/data-bases-list";
    }


    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/data-base/edit/{db_id}")
    public String dataBaseEdit(@PathVariable(value = "db_id") long db_id, Model model) {
        DataBases dataBases = dataBaseRepository.findById(db_id)
                .orElseThrow(() -> new CustomNotFoundException("Data Base not found with id " + db_id));
        model.addAttribute("dataBase", dataBases);
        //model.addAttribute("tags", tags);
        return "users/writer/edit/data-base-edit";
    }

    /**
     * Method for adding news
     *
     * @param title   Title of the news
     * @param content Content of the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/edit/{db_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String dataBaseEditPost(@PathVariable(value = "db_id") long db_id,
                                   @RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam String description,
                                   @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                   Model model) {
        try {
            // Validate file type for cover image

            DataBases existingDataBase = dataBaseRepository.findDataBasesByDbId(db_id);

            // Save the news article to the database
            existingDataBase.setTitle(title);
            existingDataBase.setContent(content);
            existingDataBase.setDescription(description);

            existingDataBase.setVisible(visible);
            dataBaseRepository.save(existingDataBase);

            model.addAttribute("message", "DataBase edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit dataBase: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/delete/{db_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteDataBase(@PathVariable("db_id") Long dbId) {
        DataBases dataBase = dataBaseRepository.findById(dbId).orElseThrow();
        dataBaseRepository.delete(dataBase);
        return "config/back";
    }

    @GetMapping("/data-base/{dbId}")
    public String viewDataBase(@PathVariable("dbId") Long dbId, Model model, Principal principal) {
        Optional<DataBases> dataBasesOptional = dataBasesServices.findById(dbId); // Получение новости по ID
        if (dataBasesOptional.isEmpty()) {
            return "users/error"; // Вернуть страницу с ошибкой
        }

        DataBases dataBases = dataBasesOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!dataBases.getVisible() && !isWriter) {
            return "redirect:/";
        }

        model.addAttribute("dataBase", dataBases);
        return "views/data-base-view"; // Вернуть шаблон для отображения новости
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
        model.addAttribute("emptyOrAllHidden", onlineServicesServices.isOnlineServicesListEmptyOrAllHidden(onlineServicesList));
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

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/publication/add")
    public String PublicationAdd(Model model) {
        model.addAttribute("publication", new PublicationActivities());
        return "users/writer/add/publication-add";
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

            publicationActivitiesServices.savePubActive(publicationActivities, cover);
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
                publicationActivitiesServices.savePubActiveImage(publicationActivities, cover);
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
    public String PublicationPageEdit(@PathVariable(value = "page_id") long pageId,Model model) {
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
