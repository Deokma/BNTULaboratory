package by.bntu.laboratory.services;

import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.PublicationImages;
import by.bntu.laboratory.repo.ProjectsRepository;
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
public class ProjectsServices {
    @Autowired
    ProjectsRepository projectsRepository;

    public void saveProjects(Projects projects, MultipartFile file) throws IOException {
        PublicationImages publicationImages;
        if (file.getSize() != 0) {
            publicationImages = toImageEntity(file);
            publicationImages.setPreviewImage(true);
            projects.addImageToProjects(publicationImages);
        }
         log.info("Saving new Project. Title: {}", projects.getTitle());
        Projects projectFromDb = projectsRepository.save(projects);
        projectFromDb.setPreviewImageId(projectFromDb.getCover().getId());
        projectsRepository.save(projects);
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

    public List<Projects> getAll() {
        return projectsRepository.findAll();
    }

    public Optional<Projects> findById(Long imageId) {
        return projectsRepository.findById(imageId);
    }

    public void deleteProjets(Long id) {
        projectsRepository.deleteById(id);
    }
    @Transactional
    public List<Projects> findProjectsByTagId(Long tagId) {
        return projectsRepository.findProjectsByTags_TagId(tagId);
    }
}
