package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;
    private String name;
    private String color;
    private int extraFare;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color, int extraFare) {
        this(null, name, color, extraFare);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, 0);
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(id, name, color, extraFare, new Sections());
    }

    public Line(Long id, String name, String color, Sections sections) {
        this(id, name, color, 0, sections);
    }

    public Line(Long id, String name, String color, int extraFare, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public int getDistance() {
        return sections.getSections().stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public List<Section> sections() {
        return sections.getSections();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        sections.addSection(section);
    }

    public void addSection(Section section) {
        if (section == null) {
            return;
        }
        sections.addSection(section);
    }

    public void removeSection(Station station) {
        sections.removeStation(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public int getExtraFare() {
        return extraFare;
    }

    public void modifyName(String name) {
        this.name = name;
    }

    public void modifyColor(String color) {
        this.color = color;
    }
}
