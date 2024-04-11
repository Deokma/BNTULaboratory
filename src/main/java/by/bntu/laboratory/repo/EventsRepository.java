package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.EventsCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends JpaRepository<EventsCalendar, Long> {

}
