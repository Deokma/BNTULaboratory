package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.Projects;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {
  //  List<Projects> findLast6ByOrderByDateDesc(Pageable projects);
   List<Projects> findProjectsByTags_TagId(Long tagId);
   Projects findByProjectId(Long tagId);
}
