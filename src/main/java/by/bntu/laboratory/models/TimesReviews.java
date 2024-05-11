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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "times_id")
    private Long timesId;

    @Column(name = "title")
    private String title;
    /*@Column(name = "description")
    private String description;*/

   /* @Column(name = "content", columnDefinition = "TEXT")
    private String content;*/

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "date")
    private Date date;

    @Column(name = "link")
    private String link;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "times_reviews_tags",
            joinColumns = @JoinColumn(
                    name = "times_id", referencedColumnName = "times_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags; // Adjusted attribute name to avoid confusion

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "timesReviews")
    private PublicationImages cover = new PublicationImages();
    private Long previewImageId;

    public void addImageToTimes(PublicationImages cove) {
        cove.setTimesReviews(this);
        cover = cove;
    }
}
