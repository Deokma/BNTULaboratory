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


   /* @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "db_tags",
            joinColumns = @JoinColumn(
                    name = "db_id", referencedColumnName = "db_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags; // Adjusted attribute name to avoid confusion
*/
}
