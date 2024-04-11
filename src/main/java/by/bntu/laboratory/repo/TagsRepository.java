package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    // Add custom query methods if needed
    Tags findByName(String name);

}
