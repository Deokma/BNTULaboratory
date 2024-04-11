package by.bntu.laboratory.services;

import by.bntu.laboratory.models.EventsCalendar;
import by.bntu.laboratory.repo.EventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventsServices {
    @Autowired
    EventsRepository eventsRepository;

    public void saveNews(EventsCalendar eventsCalendar, MultipartFile file) {
        try {
            eventsCalendar.setCover(file.getBytes());
            eventsRepository.save(eventsCalendar);
        } catch (Exception e) {
            log.debug("Some internal error occurred", e);
        }
    }

    public List<EventsCalendar> getAll() {
        return eventsRepository.findAll();
    }

    public Optional<EventsCalendar> findById(Long imageId) {
        return eventsRepository.findById(imageId);
    }
}
