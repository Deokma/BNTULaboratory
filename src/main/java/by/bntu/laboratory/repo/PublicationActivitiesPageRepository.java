package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.PublicationActivitiesPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationActivitiesPageRepository extends JpaRepository<PublicationActivitiesPage, Long> {

    Optional<PublicationActivitiesPage> findByPublicationActivities_PublIdAndPageId(Long publId, Long pageId);

    PublicationActivitiesPage findByTitle(String title);

    PublicationActivitiesPage findByPageId(Long pageId);
}
