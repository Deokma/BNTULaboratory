package by.bntu.laboratory.services;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.NewsRepository;
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
public class NewsServices {
    @Autowired
    NewsRepository newsRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;
    public void saveNews(News news, MultipartFile file) throws IOException {
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
public List<News> findNewsByTagId(Long tagId) {return newsRepository.findByTags_TagId(tagId);}
    public List<News> getAll() {
        return newsRepository.findAll();
    }

    public Optional<News> findById(Long newsId) {
        return newsRepository.findById(newsId);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
    public boolean isNewsListEmptyOrAllHidden(List<News> newsList) {
        return newsList.isEmpty() || newsList.stream().noneMatch(News::getVisible);
    }
}
