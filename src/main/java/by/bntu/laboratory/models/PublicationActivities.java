package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "publication_activities")
public class PublicationActivities implements Publications{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publ_id")
    private Long publId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @Column(name = "visible")
    private Boolean visible;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publicationActivities")
    private PublicationImages cover = new PublicationImages();
    private Long previewImageId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publicationActivities")
    private List<PublicationActivitiesPage> publicationActivitiesPages;
    public void addImageToPublActivities(PublicationImages cove) {
        cove.setPublicationActivities(this);
        cover = cove;
    }
}
