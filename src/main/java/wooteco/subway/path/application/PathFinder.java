package wooteco.subway.path.application;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.path.domain.SectionEdge;
import wooteco.subway.path.domain.SubwayGraph;
import wooteco.subway.path.domain.SubwayPath;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathFinder {
    public SubwayPath findPath(List<Line> lines, Station departure, Station arrival) {
        if (departure.equals(arrival)) {
            throw new InvalidPathException("출발역과 도착역은 같을 수 없습니다!");
        }
        SubwayGraph graph = new SubwayGraph(SectionEdge.class);
        System.out.println("여기?211111");
        graph.addVertexWith(lines);
        System.out.println("여기?22222");
        graph.addEdge(lines);

        System.out.println("여기?3333333");

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, SectionEdge> path = dijkstraShortestPath.getPath(departure, arrival);
        if (path == null) {
            throw new InvalidPathException("연결되어 있지 않은 경로입니다.");
        }

        return convertSubwayPath(path);
    }

    private SubwayPath convertSubwayPath(GraphPath graphPath) {
        List<SectionEdge> edges = (List<SectionEdge>) graphPath.getEdgeList().stream().collect(Collectors.toList());
        List<Station> stations = graphPath.getVertexList();
        return new SubwayPath(edges, stations);
    }
}
