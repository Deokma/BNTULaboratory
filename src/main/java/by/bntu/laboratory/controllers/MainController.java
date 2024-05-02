package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.EventsCalendar;
import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.EventsRepository;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.ProjectsRepository;
import by.bntu.laboratory.repo.TimesRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private Authentication authentication;
    @Autowired
    private final NewsRepository newsRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private final ProjectsRepository projectsRepository;
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private TimesRepository timesRepository;


    /**
     * Go to main page
     */
    @Transactional
    @GetMapping(value = "/", produces = MediaType.IMAGE_JPEG_VALUE)
    public String main(Model model) {
        Pageable newsPageable = PageRequest.of(0, 4); // Получаем только последние 4 элемента
        List<News> latestNews = newsRepository.findLast3ByOrderByDateDesc(newsPageable);
        Pageable projectsPageable = PageRequest.of(0, 6); // Получаем только последние 4 элемента
        List<Projects> projectsList = projectsRepository.findAll();
        Pageable eventsPageable = PageRequest.of(0, 3); // Получаем только последние 4 элемента
        List<EventsCalendar> eventsCalendarsList = eventsRepository.findLast3ByOrderByDateDesc(eventsPageable);
        Pageable timesPageable = PageRequest.of(0, 3); // Получаем только последние 4 элемента
        List<TimesReviews> timesCalendarsList = timesRepository.findLast3ByOrderByDateDesc(timesPageable);

        model.addAttribute("latestNews", latestNews);
        model.addAttribute("projectsList", projectsList);
        model.addAttribute("eventsList", eventsCalendarsList);
        model.addAttribute("timesList", timesCalendarsList);

        return "index";
    }

}