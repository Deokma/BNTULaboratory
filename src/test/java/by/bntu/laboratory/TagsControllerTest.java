package by.bntu.laboratory;

import by.bntu.laboratory.controllers.TagsController;
import by.bntu.laboratory.models.*;
import by.bntu.laboratory.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TagsControllerTest {

    @Mock
    private NewsService newsService;

    @Mock
    private TimesService timesService;

    @Mock
    private EventsService eventsService;

    @Mock
    private ProjectsService projectsService;

    @Mock
    private Model model;

    @InjectMocks
    private TagsController tagsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void viewNewsByTag() {
        List<News> newsList = Arrays.asList(new News(), new News());
        when(newsService.findNewsByTagId(1L)).thenReturn(newsList);

        String view = tagsController.viewNewsByTag(1L, model);

        verify(model, times(1)).addAttribute("newsList", newsList);
        assertEquals("views/lists/news-list", view);
    }

    @Test
    void viewEventsByTag() {
        List<Events> eventsList = Arrays.asList(new Events(), new Events());
        when(eventsService.findEventsByTagId(1L)).thenReturn(eventsList);

        String view = tagsController.viewEventsByTag(1L, model);

        verify(model, times(1)).addAttribute("eventsList", eventsList);
        assertEquals("views/lists/event-list", view);
    }

    @Test
    void viewTimesByTag() {
        List<TimesReviews> timesList = Arrays.asList(new TimesReviews(), new TimesReviews());
        when(timesService.findTimesByTagId(1L)).thenReturn(timesList);

        String view = tagsController.viewTimesByTag(1L, model);

        verify(model, times(1)).addAttribute("timesList", timesList);
        assertEquals("views/lists/times-list", view);
    }

    @Test
    void viewCalendarByTag() {
        List<Events> eventCalendarList = Arrays.asList(new Events(), new Events());
        when(eventsService.findEventsByTagId(1L)).thenReturn(eventCalendarList);

        String view = tagsController.viewCalendarByTag(1L, model);

        verify(model, times(1)).addAttribute("eventCalendarList", eventCalendarList);
        assertEquals("views/lists/event-list", view);
    }

    @Test
    void viewProjectsByTag() {
        List<Projects> projectList = Arrays.asList(new Projects(), new Projects());
        when(projectsService.findProjectsByTagId(1L)).thenReturn(projectList);

        String view = tagsController.viewProjectsByTag(1L, model);

        verify(model, times(1)).addAttribute("projectList", projectList);
        assertEquals("views/lists/project-list", view);
    }
}
