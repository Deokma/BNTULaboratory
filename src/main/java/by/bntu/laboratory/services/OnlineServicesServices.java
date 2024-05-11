package by.bntu.laboratory.services;

import by.bntu.laboratory.models.DataBases;
import by.bntu.laboratory.models.OnlineServices;
import by.bntu.laboratory.repo.DataBaseRepository;
import by.bntu.laboratory.repo.OnlineServicesRepository;
import by.bntu.laboratory.repo.PublicationImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OnlineServicesServices {
    @Autowired
    OnlineServicesRepository onlineServicesRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;
    /*public void saveNews(News news, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            news.addImageToNews(publicationImages);

        }
        News newsFromDb = newsRepository.save(news);
        newsFromDb.setPreviewImageId(newsFromDb.getCover().getId());
//        log.info("Saving new News. Title: {}; Data: {}", news.getTitle(), news.getDate());

        newsRepository.save(news);
    }
    public void saveNewsImage(News news, MultipartFile file) throws IOException {
        if (file.getSize() != 0) {
            News newsFromDb = newsRepository.findByNewsId(news.getNewsId());
            if (newsFromDb != null && newsFromDb.getCover() != null) {
                PublicationImages oldCover = newsFromDb.getCover();

                // Удалить связь
                newsFromDb.setCover(null);
                newsRepository.save(newsFromDb);

                // Удалить старое изображение
                publicationImageRepository.delete(oldCover);
            }


            // Добавить новое изображение
            PublicationImages publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            publicationImageRepository.save(publicationImages); // Сохранить изображение в базе данных
            assert newsFromDb != null;
            newsFromDb.addImageToNews(publicationImages);
            newsFromDb.setPreviewImageId(publicationImages.getId());
            newsRepository.save(newsFromDb);

        }
    }*/

    public List<OnlineServices> getAll() {
        return onlineServicesRepository.findAll();
    }
    public Optional<OnlineServices> findById(Long dbId) {
        return onlineServicesRepository.findById(dbId);
    }
    public void deleteOnlineService(Long id) {
        onlineServicesRepository.deleteById(id);
    }
    public boolean isOnlineServicesListEmptyOrAllHidden(List<OnlineServices> onlineServicesList) {
        return onlineServicesList.isEmpty() || onlineServicesList.stream().noneMatch(OnlineServices::getVisible);
    }
}
