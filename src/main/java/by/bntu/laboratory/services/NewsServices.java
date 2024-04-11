package by.bntu.laboratory.services;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.NewsRepository;
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

    public void saveNews(News news, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            news.addImageToNews(publicationImages);
        }
//        log.info("Saving new News. Title: {}; Data: {}", news.getTitle(), news.getDate());
        News newsFromDb = newsRepository.save(news);
        newsFromDb.setPreviewImageId(newsFromDb.getCover().getId());
        newsRepository.save(news);
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

    public List<News> getAll() {
        return newsRepository.findAll();
    }

    public Optional<News> findById(Long imageId) {
        return newsRepository.findById(imageId);
    }
    public News findByTitle(String title) {
        return newsRepository.findByTitle(title);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

}
