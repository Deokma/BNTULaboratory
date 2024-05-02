package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.EventsCalendar;
import by.bntu.laboratory.models.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<EventsCalendar, Long> {
    List<EventsCalendar> findTop3ByOrderByDateDesc(Pageable events);
    List<EventsCalendar> findLast3ByOrderByDateDesc(Pageable events);
    List<EventsCalendar> findEventsCalendarByTags_TagId(Long tagId);
}
