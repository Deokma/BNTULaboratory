package by.bntu.laboratory.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publication_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicationImages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "originalFileName")
    private String originalFileName;
    @Column(name = "size")
    private Long size;
    @Column(name = "contentType")
    private String contentType;
    @Column(name = "isPreviewImage")
    private boolean isPreviewImage;

    private byte[] bytes;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private News news;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private EventsCalendar eventsCalendar;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private TimesReviews timesReviews;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Projects projects;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private PublicationActivities publicationActivities;
}