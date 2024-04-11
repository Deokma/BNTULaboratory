package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.PublicationImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class PublicationImagesController {
    private final PublicationImageRepository publicationImageRepository;

    @GetMapping("/images/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        PublicationImages publicationImages = publicationImageRepository.findById(id).orElse(null);
        if (publicationImages != null) {
            return ResponseEntity.ok()
                    .header("fileName", publicationImages.getOriginalFileName())
                    .contentType(MediaType.valueOf(publicationImages.getContentType()))
                    .contentLength(publicationImages.getSize())
                    .body(new InputStreamResource(new ByteArrayInputStream(publicationImages.getBytes())));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/static/images/{filename}")
    public ResponseEntity<Resource> getStaticImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("src/main/resources/static/images/" + filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Не удалось прочитать файл: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Ошибка URL для файла: " + filename, ex);
        }
    }



}
