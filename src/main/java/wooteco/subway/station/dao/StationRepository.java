package wooteco.subway.station.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import wooteco.subway.station.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsByName(String name);
}
