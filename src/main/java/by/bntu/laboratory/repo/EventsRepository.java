package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.Events;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    List<Events> findTop3ByOrderByDateDesc(Pageable events);
    List<Events> findLast3ByOrderByDateDesc(Pageable events);
    List<Events> findEventsByTags_TagId(Long tagId);
    Events findEventsByEventId(Long id);
    List<Events> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String description);

}
