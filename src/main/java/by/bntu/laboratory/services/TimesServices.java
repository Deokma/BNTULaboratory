package by.bntu.laboratory.services;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.TimesRepository;
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
public class TimesServices {
    @Autowired
    TimesRepository timesRepository;

    public void saveTimes(TimesReviews timesReviews, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            timesReviews.addImageToTimes(publicationImages);
        }
//        log.info("Saving new News. Title: {}; Data: {}", news.getTitle(), news.getDate());
        TimesReviews timesFromDb = timesRepository.save(timesReviews);
        timesFromDb.setPreviewImageId(timesFromDb.getCover().getId());
        timesRepository.save(timesReviews);
    }
    public List<TimesReviews> getAll() {
        return timesRepository.findAll();
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
    @Transactional
    public List<TimesReviews> findTimesByTagId(Long tagId) {return timesRepository.findTimesReviewsByTags_TagId(tagId);}
    public Optional<TimesReviews> findById(Long imageId) {
        return timesRepository.findById(imageId);
    }
}
