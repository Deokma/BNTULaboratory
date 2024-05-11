package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "publication_activities_page")
public class PublicationActivitiesPage implements Publications{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long pageId;
    @Column(name = "title")
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "visible")
    private Boolean visible;
    @ManyToOne
    @JoinColumn(name = "publ_id")
    private PublicationActivities publicationActivities;
}
