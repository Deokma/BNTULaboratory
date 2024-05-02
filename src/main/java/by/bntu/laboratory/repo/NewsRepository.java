package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // You can add custom query methods here if needed
    List<News> findLast3ByOrderByDateDesc(Pageable news);
    News findByTitle(String title);

    List<News> findNewsByTags_TagId(Long tagId);
    List<News> findByTags_TagId(Long tagIds);
}
