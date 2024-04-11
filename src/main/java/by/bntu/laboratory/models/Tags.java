package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Data
@Table(name = "tags")
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long tag_id;
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "tags")
    private Collection<News> news;

//    @ManyToMany(mappedBy = "tags")
//    private Collection<EventsCalendar> eventsCalendars;
//
//    @ManyToMany(mappedBy = "tags")
//    private Collection<TimesReviews> timesReviews;
}
