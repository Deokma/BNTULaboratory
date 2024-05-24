package by.bntu.laboratory.services;

import by.bntu.laboratory.models.PublicationActivities;
import by.bntu.laboratory.models.PublicationActivitiesPage;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.PublicationActivitiesPageRepository;
import by.bntu.laboratory.repo.PublicationActivitiesRepository;
import by.bntu.laboratory.repo.PublicationImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class PublicationActivitiesService {
    @Autowired
    PublicationActivitiesRepository publicationActivitiesRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;
    @Autowired
    private PublicationActivitiesPageRepository publicationActivitiesPageRepository;


    public void savePubActive(PublicationActivities pubActive, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            pubActive.addImageToPublActivities(publicationImages);
        }
        PublicationActivities pubActiveFromDb = publicationActivitiesRepository.save(pubActive);
        pubActiveFromDb.setPreviewImageId(pubActiveFromDb.getCover().getId());
//        log.info("Saving new News. Title: {}; Data: {}", news.getTitle(), news.getDate());

        publicationActivitiesRepository.save(pubActiveFromDb);
    }

    public void savePubActiveImage(PublicationActivities pubActive, MultipartFile file) throws IOException {
        if (file.getSize() != 0) {
            PublicationActivities pubActiveFromDb = publicationActivitiesRepository.findByPublId(pubActive.getPublId());
            if (pubActiveFromDb != null && pubActiveFromDb.getCover() != null) {
                PublicationImages oldCover = pubActiveFromDb.getCover();

                // Удалить связь
                pubActiveFromDb.setCover(null);
                publicationActivitiesRepository.save(pubActiveFromDb);

                // Удалить старое изображение
                publicationImageRepository.delete(oldCover);
            }


            // Добавить новое изображение
            PublicationImages publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            publicationImageRepository.save(publicationImages); // Сохранить изображение в базе данных
            assert pubActiveFromDb != null;
            pubActiveFromDb.addImageToPublActivities(publicationImages);
            pubActiveFromDb.setPreviewImageId(publicationImages.getId());
            publicationActivitiesRepository.save(pubActiveFromDb);

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

    public List<PublicationActivities> getAll() {
        return publicationActivitiesRepository.findAll();
    }

    public PublicationActivities findById(Long id) {
        return publicationActivitiesRepository.findById(id).orElseThrow(() -> new NotFoundException("Publication not found"));
    }
    public PublicationActivitiesPage findPageById(Long publId, Long pageId) {
        return publicationActivitiesPageRepository.findByPublicationActivities_PublIdAndPageId(publId, pageId)
                .orElseThrow(() -> new NotFoundException("Page not found"));
    }
    public Long getFirstPageId(Long publId) {
        PublicationActivities publication = findById(publId);
        return publication.getPublicationActivitiesPages().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No pages found for this publication"))
                .getPageId();
    }
    public PublicationActivities findByTitle(String title) {
        return publicationActivitiesRepository.findByTitle(title);
    }

    public void deletePubActive(Long id) {
        publicationActivitiesRepository.deleteById(id);
    }

    public boolean isPubActiveListEmptyOrAllHidden(List<PublicationActivities> publicationActivitiesList) {
        return publicationActivitiesList.isEmpty() || publicationActivitiesList.stream().noneMatch(PublicationActivities::getVisible);
    }
}
