package by.bntu.laboratory.services;

import by.bntu.laboratory.models.PublicationActivities;
import by.bntu.laboratory.models.PublicationActivitiesPage;
import by.bntu.laboratory.repo.PublicationActivitiesPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PublicationActivitiesPageServices {
    @Autowired
    PublicationActivitiesPageRepository publicationActivitiesPageRepository;


    public List<PublicationActivitiesPage> getAll() {
        return publicationActivitiesPageRepository.findAll();
    }

    public Optional<PublicationActivitiesPage> findById(Long pubId) {
        return publicationActivitiesPageRepository.findById(pubId);
    }

    public PublicationActivitiesPage findByTitle(String title) {
        return publicationActivitiesPageRepository.findByTitle(title);
    }

    public void deletePubActivePage(Long id) {
        publicationActivitiesPageRepository.deleteById(id);
    }

    public boolean isPubActiveListEmptyOrAllHidden(List<PublicationActivities> publicationActivitiesList) {
        return publicationActivitiesList.isEmpty() || publicationActivitiesList.stream().noneMatch(PublicationActivities::getVisible);
    }
}
