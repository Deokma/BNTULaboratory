package by.bntu.laboratory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import by.bntu.laboratory.models.Role;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    // Role findRoleByRole_id(long id);
}
