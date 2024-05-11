package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.bntu.laboratory.models.Role;

import java.util.List;

@Repository
public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, Long> {
    boolean existsByCode(String code);
    RegistrationCode findByCode(String code);
    List<RegistrationCode> findByRole(Role role);
    void deleteRegistrationCodeByCode(String code);

}
