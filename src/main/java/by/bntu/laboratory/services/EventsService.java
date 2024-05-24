package by.bntu.laboratory.services;

import by.bntu.laboratory.models.Events;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.EventsRepository;
import by.bntu.laboratory.repo.PublicationImageRepository;
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
public class EventsService {
    @Autowired
    EventsRepository eventsRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;
    public void saveEvent(Events events, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            events.addImageToEvent(publicationImages);
        }
        log.info("Saving new Project. Title: {}", events.getTitle());
        Events eventsFromDb = eventsRepository.save(events);
        eventsFromDb.setPreviewImageId(eventsFromDb.getCover().getId());
        eventsRepository.save(eventsFromDb);
    }
    public void saveEventImage(Events events, MultipartFile file) throws IOException {
        if (file.getSize() != 0) {
            Events eventCalendarFromDb =
                    eventsRepository.findEventsByEventId(events.getEventId());
            if (eventCalendarFromDb != null && eventCalendarFromDb.getCover() != null) {
                PublicationImages oldCover = eventCalendarFromDb.getCover();

                // Удалить связь
                eventCalendarFromDb.setCover(null);
                eventsRepository.save(eventCalendarFromDb);

                // Удалить старое изображение
                publicationImageRepository.delete(oldCover);
            }


            // Добавить новое изображение
            PublicationImages publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            publicationImageRepository.save(publicationImages); // Сохранить изображение в базе данных
            assert eventCalendarFromDb != null;
            eventCalendarFromDb.addImageToEvent(publicationImages);
            eventCalendarFromDb.setPreviewImageId(publicationImages.getId());
            eventsRepository.save(eventCalendarFromDb);

        }
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
    public List<Events> getAll() {
        return eventsRepository.findAll();
    }
    @Transactional
    public List<Events> findEventsByTagId(Long tagId) {
        return eventsRepository.findEventsByTags_TagId(tagId);
    }
    public boolean isEventsListEmptyOrAllHidden(List<Events> eventsList) {
        return eventsList.isEmpty() || eventsList.stream().noneMatch(Events::getVisible);
    }
    public Optional<Events> findById(Long imageId) {
        return eventsRepository.findById(imageId);
    }
}
