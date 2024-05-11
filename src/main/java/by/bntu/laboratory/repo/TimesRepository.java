package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.TimesReviews;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimesRepository extends JpaRepository<TimesReviews, Long> {
    List<TimesReviews> findLast3ByOrderByDateDesc(Pageable times);
    List<TimesReviews> findTimesReviewsByTags_TagId(Long tagId);
    TimesReviews findByTimesId(Long timesId);
}
