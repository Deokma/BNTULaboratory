package by.bntu.laboratory.services;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.PublicationActivities;
import by.bntu.laboratory.models.PublicationActivitiesPage;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.PublicationActivitiesRepository;
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
public class PublicationActivitiesServices {
    @Autowired
    PublicationActivitiesRepository publicationActivitiesRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;

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

    public Optional<PublicationActivities> findById(Long pubId) {
        return publicationActivitiesRepository.findById(pubId);
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
