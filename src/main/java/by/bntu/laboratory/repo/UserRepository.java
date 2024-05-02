package by.bntu.laboratory.repo;

import by.bntu.laboratory.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Denis Popolamov
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String username);
}