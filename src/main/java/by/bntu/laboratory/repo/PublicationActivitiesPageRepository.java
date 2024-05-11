package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.PublicationActivities;
import by.bntu.laboratory.models.PublicationActivitiesPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationActivitiesPageRepository extends JpaRepository<PublicationActivitiesPage, Long> {


    PublicationActivitiesPage findByTitle(String title);

    PublicationActivitiesPage findByPageId(Long pageId);
}
