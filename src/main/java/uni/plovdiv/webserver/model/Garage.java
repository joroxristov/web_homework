package uni.plovdiv.webserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "garage")
@Getter
@Setter
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String location;

    private String city;

    private Integer capacity;

    @ManyToMany(mappedBy = "garages", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Car> cars;
}
