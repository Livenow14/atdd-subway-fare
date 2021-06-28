package wooteco.subway.line.domain;

import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.InvalidInputException;
import wooteco.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        checkAlreadyExisted(section);
        checkExistedAny(section);

        addSectionUpToUp(section);
        addSectionDownToDown(section);

        this.sections.add(section);
    }

    private void checkExistedAny(Section section) {
        List<Station> stations = getStations();
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new InvalidInputException(String.format("노선에 등록하려는 구간의 역들이 포함되어있지 않습니다. 입력한 역 이름: %s, %s", section.getUpStation().getName(), section.getDownStation().getName()));
        }
    }

    private void checkAlreadyExisted(Section section) {
        List<Station> stations = getStations();
        List<Station> stationsOfNewSection = Arrays.asList(section.getUpStation(), section.getDownStation());
        if (stations.containsAll(stationsOfNewSection)) {
            throw new DuplicateException("이미 등록된 구간입니다");
        }
    }

    private void addSectionUpToUp(Section section) {
        this.sections.stream()
                .filter(it -> it.getUpStation().isSameStation(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> replaceSectionWithDownStation(section, it));
    }

    private void addSectionDownToDown(Section section) {
        this.sections.stream()
                .filter(it -> it.getDownStation().isSameStation(section.getDownStation()))
                .findFirst()
                .ifPresent(it -> replaceSectionWithUpStation(section, it));
    }

    private void replaceSectionWithUpStation(Section newSection, Section existSection) {
        if (existSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidInputException("등록하려는 구간의 길이가 기존의 구간보다 깁니다. 기존 구간의 길이 " + existSection.getDistance());
        }
        this.sections.add(new Section(existSection.getUpStation(), newSection.getUpStation(), existSection.getDistance() - newSection.getDistance()));
        this.sections.remove(existSection);
    }

    private void replaceSectionWithDownStation(Section newSection, Section existSection) {
        if (existSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidInputException("등록하려는 구간의 길이가 기존의 구간보다 깁니다. 기존 구간의 길이 " + existSection.getDistance());
        }
        this.sections.add(new Section(newSection.getDownStation(), existSection.getDownStation(), existSection.getDistance() - newSection.getDistance()));
        this.sections.remove(existSection);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());

        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }

        return stations;
    }

    private Section findUpEndSection() {
        List<Station> downStations = this.sections.stream()
                .map(it -> it.getDownStation())
                .collect(Collectors.toList());

        Section section = this.sections.stream()
                .filter(it -> downStations.stream()
                        .noneMatch(dit -> dit.isSameStation(it.getUpStation())))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        return section;
    }

    private Section findSectionByNextUpStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().isSameStation(station))
                .findFirst()
                .orElse(null);
    }

    public void removeStation(Station station) {
        if (sections.size() <= 1) {
            throw new InvalidInputException("구간이 하나일 경우 삭제할 수 없습니다.");
        }

        Optional<Section> upSection = sections.stream()
                .filter(it -> it.getUpStation().isSameStation(station))
                .findFirst();
        Optional<Section> downSection = sections.stream()
                .filter(it -> it.getDownStation().isSameStation(station))
                .findFirst();

        if (upSection.isPresent() && downSection.isPresent()) {
            Station newUpStation = downSection.get().getUpStation();
            Station newDownStation = upSection.get().getDownStation();
            int newDistance = upSection.get().getDistance() + downSection.get().getDistance();
            sections.add(new Section(newUpStation, newDownStation, newDistance));
        }

        upSection.ifPresent(it -> sections.remove(it));
        downSection.ifPresent(it -> sections.remove(it));
    }
}
