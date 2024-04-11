package by.bntu.laboratory.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Table(name = "times_reviews")
public class TimesReviews implements Publications {

    @Id
    @Column(name = "times_id")
    private Long times_id;

    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible")
    private Boolean visible;

    @Lob
    @Column
    private byte[] cover;

    @Column(name = "date")
    private Date date;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "times_reviews_tags",
            joinColumns = @JoinColumn(
                    name = "times_id", referencedColumnName = "times_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags; // Adjusted attribute name to avoid confusion
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "times_reviews_id")
    private PublicationImages image;
}
