package by.bntu.laboratory.controllers;


import by.bntu.laboratory.models.EventsCalendar;
import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.EventsServices;
import by.bntu.laboratory.services.NewsServices;
import by.bntu.laboratory.services.ProjectsServices;
import by.bntu.laboratory.services.TimesServices;
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
    private final NewsServices newsService;
    private final TimesServices timesService;
    private final EventsServices eventCalendarService;
    private final ProjectsServices projectService;

    public TagsController(NewsServices newsService, TimesServices timesService, EventsServices eventCalendarService, ProjectsServices projectService) {
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
        return "views/news-list";
    }

    @GetMapping("/tag/{tagId}/times")
    public String viewTimesByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список времен по тегу
        List<TimesReviews> timesList = timesService.findTimesByTagId(tagId);
        model.addAttribute("timesList", timesList);
        return "views/times-list";
    }

    @GetMapping("/tag/{tagId}/calendar")
    public String viewCalendarByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список событий календаря по тегу
        List<EventsCalendar> eventCalendarList = eventCalendarService.findEventsByTagId(tagId);
        model.addAttribute("eventCalendarList", eventCalendarList);
        return "views/event-list";
    }

    @GetMapping("/tag/{tagId}/projects")
    public String viewProjectsByTag(@PathVariable("tagId") Long tagId, Model model) {
        // Получаем список проектов по тегу
        List<Projects> projectList = projectService.findProjectsByTagId(tagId);
        model.addAttribute("projectList", projectList);
        return "views/project-list";
    }
}
