package by.bntu.laboratory;
import by.bntu.laboratory.controllers.EventsController;
import by.bntu.laboratory.models.Events;
import by.bntu.laboratory.repo.EventsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.EventsService;
import by.bntu.laboratory.services.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EventsControllerTest {

    @Mock
    private EventsService eventsService;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private TagsService tagsService;

    @Mock
    private Model model;

    @InjectMocks
    private EventsController eventsController;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка замоканного SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testEvents() throws Exception {
        List<Events> eventsList = Collections.singletonList(new Events());
        when(eventsRepository.findAll()).thenReturn(eventsList);
        when(eventsService.isEventsListEmptyOrAllHidden(eventsList)).thenReturn(true);

        String result = eventsController.events(model);

        verify(model, times(1)).addAttribute(eq("eventsJson"), anyString());
        verify(model, times(1)).addAttribute("eventCalendarList", eventsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("views/lists/event-list", result);
    }

    @Test
    public void testEventAdd() {
        when(tagsRepository.findAll()).thenReturn(Collections.emptyList());

        String result = eventsController.eventAdd(model);

        verify(model, times(1)).addAttribute("event", new Events());
        verify(model, times(1)).addAttribute("tags", Collections.emptyList());
        assertEquals("users/writer/add/event-add", result);
    }

    @Test
    public void testViewEvent() {
        Events event = new Events();
        event.setVisible(true); // Устанавливаем значение для visible
        when(eventsService.findById(1L)).thenReturn(Optional.of(event));

        String result = eventsController.viewEvent(1L, model);

        verify(model, times(1)).addAttribute("event", event);
        assertEquals("views/event-view", result);
    }

    @Test
    public void testAddEvent() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.getOriginalFilename()).thenReturn("cover.jpg");
        when(tagsService.parseAndSaveTags("tag1, tag2")).thenReturn(Collections.emptyList());

        String result = eventsController.addEvent("title", "content", null, cover, "tag1, tag2", model);

        verify(eventsService, times(1)).saveEvent(any(Events.class), eq(cover));
        verify(eventsRepository, times(1)).save(any(Events.class));
        verify(model, times(1)).addAttribute("message", "Event added successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testEventEdit() {
        Events event = new Events();
        event.setTags(Collections.emptyList()); // Устанавливаем значение для eventTags
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        String result = eventsController.eventEdit(1L, model);

        verify(model, times(1)).addAttribute("event", event);
        assertEquals("users/writer/edit/event-calendar-edit", result);
    }

    @Test
    public void testEventEditPost() throws Exception {
        MultipartFile cover = mock(MultipartFile.class);
        when(cover.getOriginalFilename()).thenReturn("cover.jpg");
        Events event = new Events();
        event.setTags(Collections.emptyList()); // Устанавливаем значение для eventTags
        when(eventsRepository.findEventsByEventId(1L)).thenReturn(event);

        String result = eventsController.eventEditPost(1L, "title", "content", null, cover, "tag1, tag2", true, model);

        verify(eventsService, times(1)).saveEventImage(event, cover);
        verify(eventsRepository, times(1)).save(event);
        verify(model, times(1)).addAttribute("message", "Event edited successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testDeleteEvent() {
        Events event = new Events();
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        String result = eventsController.deleteEvent(1L);

        verify(eventsRepository, times(1)).delete(event);
        assertEquals("config/back", result);
    }

    @Test
    public void testHiddenEvent() {
        List<Events> eventsList = Collections.singletonList(new Events());
        when(eventsRepository.findAll()).thenReturn(eventsList);
        when(eventsService.isEventsListEmptyOrAllHidden(eventsList)).thenReturn(true);

        String result = eventsController.hiddenEvent(model);

        verify(model, times(1)).addAttribute("eventsCalendarList", eventsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("users/writer/hidden/events-list", result);
    }
}
