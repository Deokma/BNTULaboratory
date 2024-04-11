package by.bntu.laboratory.services;

import by.bntu.laboratory.models.Role;
import by.bntu.laboratory.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> findRolesByIds(List<Long> roleIds) {
        Set<Long> validRoleIds = new HashSet<>();
        for (Long roleId : roleIds) {
            try {
                Long id = Long.parseLong(String.valueOf(roleId));
                validRoleIds.add(id);
            } catch (NumberFormatException e) {
                // Handle invalid role IDs here (optional)
            }
        }

        return roleRepository.findAllById(validRoleIds);
    }


    // Other methods if needed, e.g., findById, save, delete, etc.
}
