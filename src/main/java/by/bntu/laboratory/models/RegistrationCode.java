package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "registration_codes")
public class RegistrationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role; // Ссылка на роль, для которой создан данный код

    // Конструкторы, геттеры и сеттеры...
}

