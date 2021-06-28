package wooteco.subway.line.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import wooteco.subway.line.domain.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
    boolean existsByName(String name);

    boolean existsByColor(String color);
}
