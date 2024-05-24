package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.DataBases;
import by.bntu.laboratory.models.OnlineServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnlineServicesRepository extends JpaRepository<OnlineServices, Long> {

    // You can add custom query methods here if needed
    List<OnlineServices> findTop3ByOrderByOsIDDesc();

    OnlineServices findByTitle(String title);

    OnlineServices findOnlineServicesByOsID(Long osId);
    List<OnlineServices> findByTitleContainingIgnoreCase(String title);

}
