package wooteco.subway.station.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        Station station = (Station) o;
        return this.id.equals(station.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
