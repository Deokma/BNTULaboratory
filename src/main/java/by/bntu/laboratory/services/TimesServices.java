package by.bntu.laboratory.services;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.TimesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TimesServices {
    @Autowired
    TimesRepository timesRepository;
    public void saveNews(TimesReviews timesReviews, MultipartFile file) {
        try {
            timesReviews.setCover(file.getBytes());
            timesRepository.save(timesReviews);
        } catch (Exception e) {
            log.debug("Some internal error occurred", e);
        }
    }
    public List<TimesReviews> getAll() {
        return timesRepository.findAll();
    }

    public Optional<TimesReviews> findById(Long imageId) {
        return timesRepository.findById(imageId);
    }
}
