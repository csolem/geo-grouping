import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CoordinateGrouperTest {

    private CoordinateGrouper coordinateGrouper = new CoordinateGrouper();

    private GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    public void groupPoints() {
        List<Point> points = new ArrayList<>();
        points.add(geometryFactory.createPoint(new Coordinate(5, 30)));
        points.add(geometryFactory.createPoint(new Coordinate(6, 31)));

        List<List<Point>> groups = coordinateGrouper.group(points);

        assertThat(groups).hasSize(2);
    }

    @Test
    public void splitMultipleQuays() {
        List<Point> points = new ArrayList<>();

        points.add(geometryFactory.createPoint(new Coordinate(5, 30)));
        points.add(geometryFactory.createPoint(new Coordinate(6, 31)));
        points.add(geometryFactory.createPoint(new Coordinate(6.00001, 31.00001)));

        List<List<Point>> actual = coordinateGrouper.group(points);

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting(List::size).contains(2, 1);
    }

    @Test
    public void splitQuaysMultipleStops() {
        int numberOfPoints = 10;
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(geometryFactory.createPoint(new Coordinate(1 + i, 30)));
            points.add(geometryFactory.createPoint(new Coordinate(2 + i, 31)));
        }

        List<List<Point>> actual = coordinateGrouper.group(points);

        assertThat(actual).hasSize(numberOfPoints * 2);
    }

    @Test
    public void noSplit() {

        List<Point> points = new ArrayList<>();

        points.add(geometryFactory.createPoint(new Coordinate(5, 30)));

        points.add(geometryFactory.createPoint(new Coordinate(5.001, 30.0001)));

        List<List<Point>> actual = coordinateGrouper.group(points);

        assertThat(actual).hasSize(1);
    }
}