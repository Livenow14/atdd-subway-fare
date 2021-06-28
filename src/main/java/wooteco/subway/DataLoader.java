package wooteco.subway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wooteco.subway.line.dao.LineRepository;
import wooteco.subway.line.dao.SectionRepository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.member.dao.MemberDao;
import wooteco.subway.member.domain.Member;
import wooteco.subway.station.dao.StationRepository;
import wooteco.subway.station.domain.Station;

@Component
@Profile("local")
public class DataLoader implements CommandLineRunner {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final MemberDao memberDao;

    public DataLoader(StationRepository stationRepository, LineRepository lineRepository, SectionRepository sectionRepository, MemberDao memberDao) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.memberDao = memberDao;
    }

    @Override
    public void run(String... args) throws Exception {
        if (stationRepository.findAll().isEmpty()) {
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 판교역 = stationRepository.save(new Station("판교역"));
            Station 정자역 = stationRepository.save(new Station("정자역"));
            Station 역삼역 = stationRepository.save(new Station("역삼역"));
            Station 잠실역 = stationRepository.save(new Station("잠실역"));

            Line 신분당선 = lineRepository.save(new Line("신분당선", "red lighten-1", 900));
            신분당선.addSection(new Section(강남역, 판교역, 10));
            신분당선.addSection(new Section(판교역, 정자역, 10));

            Line 이호선 = lineRepository.save(new Line("2호선", "green lighten-1", 0));
            이호선.addSection(new Section(강남역, 역삼역, 10));
            이호선.addSection(new Section(역삼역, 잠실역, 10));

            Member member = new Member("email@email.com", "password", 10);
            memberDao.insert(member);
        }
    }
}

