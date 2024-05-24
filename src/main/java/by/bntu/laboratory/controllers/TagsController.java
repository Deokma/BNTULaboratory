package by.bntu.laboratory.controllers;


import by.bntu.laboratory.models.Events;
import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.EventsService;
import by.bntu.laboratory.services.NewsService;
import by.bntu.laboratory.services.ProjectsService;
import by.bntu.laboratory.services.TimesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagsController {

    @Autowired
    private TagsRepository tagsRepository;
    private final NewsService newsService;
    private final TimesService timesService;
    private final EventsService eventCalendarService;
    private final ProjectsService projectService;

    public TagsController(NewsService newsService, TimesService timesService, EventsService eventCalendarService, ProjectsService projectService) {
        this.newsService = newsService;
        this.timesService = timesService;
        this.eventCalendarService = eventCalendarService;
        this.projectService = projectService;
    }


    @GetMapping("/tag/{tagId}/news")
    public String viewNewsByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список новостей по тегу
        List<News> newsList = newsService.findNewsByTagId(tagId);
        model.addAttribute("newsList", newsList);
        return "views/lists/news-list";
    }
    @GetMapping("/tag/{tagId}/events")
    public String viewEventsByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список новостей по тегу
        List<Events> eventsList = eventCalendarService.findEventsByTagId(tagId);
        model.addAttribute("eventsList", eventsList);
        return "views/lists/event-list";
    }

    @GetMapping("/tag/{tagId}/times")
    public String viewTimesByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список времен по тегу
        List<TimesReviews> timesList = timesService.findTimesByTagId(tagId);
        model.addAttribute("timesList", timesList);
        return "views/lists/times-list";
    }

    @GetMapping("/tag/{tagId}/calendar")
    public String viewCalendarByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список событий календаря по тегу
        List<Events> eventCalendarList = eventCalendarService.findEventsByTagId(tagId);
        model.addAttribute("eventCalendarList", eventCalendarList);
        return "views/lists/event-list";
    }

    @GetMapping("/tag/{tagId}/projects")
    public String viewProjectsByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список проектов по тегу
        List<Projects> projectList = projectService.findProjectsByTagId(tagId);
        model.addAttribute("projectList", projectList);
        return "views/lists/project-list";
    }
}
