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
    @Lob
    private byte[] bytes;

    //    @ManyToOne
//    @JoinColumn(name = "publication_id")
//    private Publications publication;
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    //@JoinColumn(name = "news_id") // Имя столбца в таблице 'images', связывающего с таблицей 'news'
    private News news;
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    // @JoinColumn(name = "events_calendar_id")
    private EventsCalendar eventsCalendar;
    //
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    // @JoinColumn(name = "times_reviews_id")
    private TimesReviews timesReviews;
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    // @JoinColumn(name = "times_reviews_id")
    private Projects projects;
}