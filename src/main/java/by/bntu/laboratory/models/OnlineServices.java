package by.bntu.laboratory.models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "online_services")
public class OnlineServices implements Publications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "os_id")
    private Long osID;

    @Column(name = "title")
    private String title;

    @Column(name = "cover", columnDefinition = "TEXT")
    private String cover;
    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "link")
    private String link;

}
