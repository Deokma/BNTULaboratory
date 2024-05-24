package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.Events;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.EventsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.EventsService;
import by.bntu.laboratory.services.TagsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class EventsController {
    private final EventsService eventsService;
    private final EventsRepository eventsRepository;
    private final TagsRepository tagsRepository;
    private final TagsService tagsService;
    public EventsController(EventsService eventsService, EventsRepository eventsRepository, TagsRepository tagsRepository, TagsService tagsService) {
        this.eventsService = eventsService;
        this.eventsRepository = eventsRepository;
        this.tagsRepository = tagsRepository;
        this.tagsService = tagsService;
    }

    @GetMapping("/events")
    public String events(Model model) throws IOException {


        List<Events> events = eventsRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        String eventsJson = "";
        try {
            eventsJson = objectMapper.writeValueAsString(events);
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибки
        }

        model.addAttribute("eventsJson", eventsJson); // Добавление JSON-строки в модель
        model.addAttribute("eventCalendarList", events);
        model.addAttribute("emptyOrAllHidden", eventsService.isEventsListEmptyOrAllHidden(events));

        return "views/lists/event-list";
    }

    /**
     * Go to Add Event Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/event/add")
    public String eventAdd(Model model) {
        model.addAttribute("event", new Events());
        model.addAttribute("tags", tagsRepository.findAll());
        return "users/writer/add/event-add";
    }

    /**
     * Go to Events Page
     */
    @GetMapping("/event/{eventId}")
    public String viewEvent(@PathVariable("eventId") Long eventId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<Events> eventOptional = eventsService.findById(eventId); // Предполагается, что у вас есть сервис для работы с новостями
        if (eventOptional.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        Events events = eventOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!events.getVisible() && !isWriter) {
            return "redirect:/";
        }
        model.addAttribute("event", events);
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
            Events events = new Events();
            events.setTitle(title);
            events.setContent(content);
            events.setTags(savedTags);
            events.setVisible(true);
            events.setDate(date);
            eventsService.saveEvent(events, cover);

            eventsRepository.save(events);

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
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new CustomNotFoundException("Event not found with id " + eventId));
        Collection<Tags> eventTags = events.getTags();
        List<Long> eventTagIds = eventTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("event", events);
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

            Events existingEvent = eventsRepository.findEventsByEventId(eventId);
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
                eventsService.saveEventImage(existingEvent, cover);
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
        Events events = eventsRepository.findById(eventId).orElseThrow();
        eventsRepository.delete(events);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/events")
    public String hiddenEvent(Model model) {
        List<Events> eventsList = eventsRepository.findAll();
        model.addAttribute("eventsCalendarList", eventsList);
        model.addAttribute("emptyOrAllHidden", eventsService.isEventsListEmptyOrAllHidden(eventsList));
        return "users/writer/hidden/events-list";
    }

}
