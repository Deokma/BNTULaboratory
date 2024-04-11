package by.bntu.laboratory.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Table(name = "events_calendar")
public class EventsCalendar implements Publications {

    @Id
    @Column(name = "event_id")
    private Long event_id;

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
    @JoinTable(name = "events_calendar_tags", joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags; // Adjusted attribute name to avoid confusion
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "events_calendar_id")
    private PublicationImages image;

}
