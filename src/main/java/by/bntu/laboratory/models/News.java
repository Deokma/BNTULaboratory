package by.bntu.laboratory.models;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;

import java.util.*;

@Entity
@Data
@Table(name = "news")
public class News implements Publications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "date")
    private Date date;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "news_tags",
            joinColumns = @JoinColumn(
                    name = "news_id", referencedColumnName = "news_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "tag_id"))
    private Collection<Tags> tags; // Adjusted attribute name to avoid confusion

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "news")
    private PublicationImages cover = new PublicationImages();
    private Long previewImageId;

    public void addImageToNews(PublicationImages cove) {
        cove.setNews(this);
        cover = cove;
    }
    @Transactional
    public Collection<Tags> getTags() {
        return tags;
    }
    @Override
    public String toString() {
        return "News{id=" + newsId + ", title='" + title + '\'' + ", content='" + content + '\'' + '}';
    }
}
