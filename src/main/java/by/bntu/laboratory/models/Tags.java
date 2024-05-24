package by.bntu.laboratory.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tags")
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long tagId;
    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return "Tags{id=" + tagId + ", name='" + name + '\'' + '}';
    }

}
