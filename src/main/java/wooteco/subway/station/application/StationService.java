package wooteco.subway.station.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.InvalidInputException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.station.dao.StationRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationsRepository;
    private final SectionDao sectionDao;

    public StationService(StationRepository stationsRepository, SectionDao sectionDao) {
        this.stationsRepository = stationsRepository;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        if (stationsRepository.existsByName(stationRequest.getName())) {
            throw new DuplicateException("이미 존재하는 역 이름 입니다. (입력된 이름 값 : " + stationRequest.getName() + ")");
        }

        Station station = stationsRepository.save(stationRequest.toStation());
        return StationResponse.of(station);
    }

    public Station findStationById(Long id) {
        return stationsRepository.findById(id).orElseThrow(() -> new NotFoundException("찾을 수 없는 역입니다"));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationsRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        if (sectionDao.existsByStationId(id)) {
            throw new InvalidInputException("노선에 등록된 역은 삭제할 수 없습니다.");
        }
        if (Objects.isNull(findStationById(id))) {
            throw new NotFoundException("삭제할 역이 등록되어 있지 않습니다.");
        }
        stationsRepository.deleteById(id);
    }
}
