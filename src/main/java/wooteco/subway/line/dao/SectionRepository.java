package wooteco.subway.line.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wooteco.subway.line.domain.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("select count(s) from Section s where s.upStation.id =:stationId or s.downStation.id =:stationId")
    int existsByStationId(@Param("stationId") Long stationId);
}
