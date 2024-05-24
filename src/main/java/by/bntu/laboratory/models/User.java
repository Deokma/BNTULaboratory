package by.bntu.laboratory.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Denis Popolamov
 */
@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String email;
    @Column(name = "active")
    private boolean active;
    @Column(name = "password")
    @Size(min = 3, message = "Password may be min 3 characters")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "role_id"))
    private Collection<Role> roles;

    @Override
    public String getUsername() {
        return this.email; // Используйте поле электронной почты как имя пользователя
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Hibernate.initialize(this.roles);
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public class CurrentUser {
        private User user;

        public CurrentUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public boolean hasRole(String roleName) {
        if (this.roles == null) {
            return false;
        }
        return this.roles.stream()
                .anyMatch(role -> role.getAuthority().equals(roleName));
    }
}