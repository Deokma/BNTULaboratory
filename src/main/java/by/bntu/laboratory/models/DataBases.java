package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "data_bases")
public class DataBases implements Publications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible")
    private Boolean visible;

}
