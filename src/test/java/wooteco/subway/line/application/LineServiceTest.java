package wooteco.subway.line.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.when;

class LineServiceTest {
    public static final Station 흑기역 = new Station(1L, "흑기역");
    public static final Station 백기역 = new Station(2L, "백기역");
    public static final Station 낙성대역 = new Station(3L, "낙성대역");

    public static final Section 인천1호선_흑기백기구간 = new Section(1L, 흑기역, 백기역, 13);
    public static final Sections 인천1호선_구간 = new Sections(new ArrayList<>(Arrays.asList(인천1호선_흑기백기구간)));
    public static final Line 인천1호선 = new Line(1L, "인천1호선", "White", 인천1호선_구간);


    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;
    @Mock

    private SectionDao sectionDao;
    @Mock
    private StationDao stationDao;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateServiceEntityManagerTest() {
        //given
        Long lineId = 1L;
        new Line();
        when(lineDao.findById(lineId)).thenReturn(인천1호선);
        when(stationDao.findById(2L)).thenReturn(백기역);
        when(stationDao.findById(3L)).thenReturn(낙성대역);

        //when
        LineResponse lineResponse = lineService.addLineStation(lineId, new SectionRequest(2L, 3L, 12));

        //then
    }
}