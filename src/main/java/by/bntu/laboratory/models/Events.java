package by.bntu.laboratory.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Table(name = "events")
public class Events implements Publications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "date")
    private Date date;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_tags",
            joinColumns = @JoinColumn(
                    name = "event_id", referencedColumnName = "event_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "events")
    private PublicationImages cover = new PublicationImages();
    private Long previewImageId;

    public void addImageToEvent(PublicationImages coverImage) {
        coverImage.setEvents(this);
        cover = coverImage;
    }

}
