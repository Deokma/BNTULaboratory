/*
package by.bntu.laboratory;

import by.bntu.laboratory.controllers.PublicationImagesController;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.PublicationImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PublicationImagesControllerTest {

    @Mock
    private PublicationImageRepository publicationImageRepository;

    @InjectMocks
    private PublicationImagesController publicationImagesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getImageById_found() {
        PublicationImages image = new PublicationImages();
        image.setBytes("test".getBytes());
        image.setContentType("image/png");
        image.setOriginalFileName("test.png");
        image.setSize(4L);

        when(publicationImageRepository.findById(1L)).thenReturn(Optional.of(image));

        ResponseEntity<?> response = publicationImagesController.getImageById(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getImageById_notFound() {
        when(publicationImageRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = publicationImagesController.getImageById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getStaticImage_found() throws Exception {
        String filename = "test.png";
        UrlResource resource = new UrlResource(Paths.get("src/main/resources/static/images/" + filename).toUri());

        ResponseEntity<?> response = publicationImagesController.getStaticImage(filename);

        assertEquals(200, response.getStatusCodeValue());
    }
}
*/
