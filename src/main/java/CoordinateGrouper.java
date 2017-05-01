import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class CoordinateGrouper {

    private static final Logger logger = LoggerFactory.getLogger(CoordinateGrouper.class);
    private static final int GROUP_DISTANCE = 1000;

    public List<List<Point>> group(List<Point> points) {

        List<List<Point>> groups = new ArrayList<>();

        points.forEach(point -> {
            groupPoints(point, groups);
        });
        logger.info("Created {} groups from {} points.", groups.size(), points.size());

        return groups;
    }

    private void groupPoints(Point point, List<List<Point>> groups) {

        if (groups.isEmpty()) {
            logger.debug("Empty group. Adding point to new group");
            groups.add(new ArrayList<>(Arrays.asList(point)));
        } else {

            boolean wasGrouped = false;

            for (ListIterator<List<Point>> pointGroupIterator = groups.listIterator(); pointGroupIterator.hasNext(); ) {
                List<Point> existingPointList = pointGroupIterator.next();

                wasGrouped = addIfClose(point, existingPointList);

                if (wasGrouped) {
                    break;
                }
            }

            if (!wasGrouped) {
                groups.add(new ArrayList<>(Arrays.asList(point)));

            }
        }
    }

    private boolean addIfClose(Point point, List<Point> existingPointList) {
        for (ListIterator<Point> alreadyAddedPointIterator = existingPointList.listIterator(); alreadyAddedPointIterator.hasNext(); ) {
            Point alreadyAdded = alreadyAddedPointIterator.next();

            if (point == alreadyAdded) {
                return true;
            } else if (close(point, alreadyAdded)) {
                alreadyAddedPointIterator.add(point);
                return true;
            }
        }
        return false;
    }

    private boolean close(Point point, Point point2) {
        logger.debug("Checking distance between {} and {}", point, point2);

        try {
            double distanceInMeters = JTS.orthodromicDistance(
                    point.getCoordinate(),
                    point2.getCoordinate(),
                    DefaultGeographicCRS.WGS84);
            return distanceInMeters < GROUP_DISTANCE;
        } catch (TransformException e) {
            logger.warn("Error checking distance between {} and {}", point, point2, e);
        }
        return true;
    }
}
