package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // You can add custom query methods here if needed
    List<News> findLast3ByOrderByDateDesc(Pageable news);

   // News findByTitle(String title);
    Optional<News> findByTitle(String title);

    News findByNewsId(Long newsId);

    List<News> findNewsByTags_TagId(Long tagId);

    List<News> findByTags_TagId(Long tagIds);
    List<News> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
