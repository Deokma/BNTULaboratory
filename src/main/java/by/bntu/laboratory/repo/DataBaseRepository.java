package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.DataBases;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataBaseRepository extends JpaRepository<DataBases, Long> {

    // You can add custom query methods here if needed
    List<DataBases> findTop3ByOrderByDbIdDesc();

    DataBases findByTitle(String title);

    DataBases findDataBasesByDbId(Long dbId);

}
