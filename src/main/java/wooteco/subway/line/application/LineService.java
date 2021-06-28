package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dao.LineRepository;
import wooteco.subway.line.dao.SectionRepository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineDetailResponse;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.dao.StationRepository;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateException("이미 존재하는 노선 이름 입니다. (입력된 이름 값 : " + request.getName() + ")");
        }
        if (lineRepository.existsByColor(request.getColor())) {
            throw new DuplicateException("이미 존재하는 노선 색깔 입니다. (입력된 색깔 값 : " + request.getColor() + ")");
        }
        Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), request.getExtraFare()));
        persistLine.addSection(addInitSection(request));
        return LineResponse.of(persistLine);
    }

    private Section addInitSection(LineRequest request) {
        if (request.getUpStationId() != null && request.getDownStationId() != null) {
            Station upStation = findStationById(request.getUpStationId());
            Station downStation = findStationById(request.getDownStationId());
            Section section = new Section(upStation, downStation, request.getDistance());

            //TODO
            return sectionRepository.save(section);
        }
        return null;
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public List<Line> findLines() {
        return lineRepository.findAll();
    }

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        return LineResponse.of(persistLine);
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("찾을 수 없는 노선입니다"));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line line = findLineById(id);
        line.modifyName(lineUpdateRequest.getName());
        line.modifyColor(lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addLineStation(Long lineId, SectionRequest request) {
        Line line = findLineById(lineId);

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance());

        return LineResponse.of(line);
    }

    @Transactional
    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = findStationById(stationId);
        line.removeSection(station);
    }

    public List<LineDetailResponse> findLineDetails() {
        List<Line> lines = findLines();
        return lines.stream()
                .map(LineDetailResponse::of)
                .collect(Collectors.toList());
    }

    public LineDetailResponse findLineDetail(Long id) {
        Line line = findLineById(id);
        return LineDetailResponse.of(line);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new NotFoundException("찾을 수 없는 역입니다."));
    }
}
