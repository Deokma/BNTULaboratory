package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.PublicationActivities;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationActivitiesRepository extends JpaRepository<PublicationActivities, Long> {


    PublicationActivities findByTitle(String title);

    PublicationActivities findByPublId(Long publId);
}
