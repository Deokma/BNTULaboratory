package by.bntu.laboratory.services;

import by.bntu.laboratory.models.EventsCalendar;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.EventsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventsServices {
    @Autowired
    EventsRepository eventsRepository;

    public void saveEvent(EventsCalendar eventsCalendar, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            eventsCalendar.addImageToEvent(publicationImages);
        }
        log.info("Saving new Project. Title: {}", eventsCalendar.getTitle());
        EventsCalendar eventsFromDb = eventsRepository.save(eventsCalendar);
        eventsFromDb.setPreviewImageId(eventsFromDb.getCover().getId());
        eventsRepository.save(eventsFromDb);
    }
    private PublicationImages toImageEntity(MultipartFile file) throws IOException {
        PublicationImages image = new PublicationImages();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }
    public List<EventsCalendar> getAll() {
        return eventsRepository.findAll();
    }
    @Transactional
    public List<EventsCalendar> findEventsByTagId(Long tagId) {
        return eventsRepository.findEventsCalendarByTags_TagId(tagId);
    }
    public Optional<EventsCalendar> findById(Long imageId) {
        return eventsRepository.findById(imageId);
    }
}
