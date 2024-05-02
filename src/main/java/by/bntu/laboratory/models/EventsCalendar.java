package by.bntu.laboratory.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Table(name = "events_calendar")
public class EventsCalendar implements Publications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long event_id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "date")
    private Date date;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_calendar_tags",
            joinColumns = @JoinColumn(
                    name = "event_id", referencedColumnName = "event_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PublicationImages cover = new PublicationImages();
    private Long previewImageId;

    public void addImageToEvent(PublicationImages coverImage) {
        coverImage.setEventsCalendar(this);
        cover = coverImage;
    }
}
