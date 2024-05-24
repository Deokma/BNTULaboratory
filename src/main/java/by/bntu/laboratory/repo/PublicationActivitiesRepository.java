package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.PublicationActivities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationActivitiesRepository extends JpaRepository<PublicationActivities, Long> {


    PublicationActivities findByTitle(String title);

    PublicationActivities findByPublId(Long publId);
}
