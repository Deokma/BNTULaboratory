package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.TimesReviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesRepository extends JpaRepository<TimesReviews, Long> {
}
