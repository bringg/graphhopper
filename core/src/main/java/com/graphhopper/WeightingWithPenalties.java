package com.graphhopper;

import com.graphhopper.Penalties.Penalty;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.routing.weighting.AbstractWeighting;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WeightingWithPenalties extends AbstractWeighting {
    /**
     * Converting to seconds is not necessary but makes adding other penalties easier (e.g. turn
     * costs or traffic light costs etc)
     * speed is in km / hr
     * distance is in meters convert it to km    ->   / 1000
     * so time is in hours -> convert it to sec  ->   * 60 * 60
     */
    private final static double SPEED_CONV = 3.6;
    private static final int MIN_TO_SEC = 60;
    private static final Logger logger = LoggerFactory.getLogger(WeightingWithPenalties.class);

    private final Map<Integer, WayData> visitedEdgesCoordinates = new HashMap<>();
    private final Collection<Penalty> penalties;
    private final Weighting weightings;


    public WeightingWithPenalties(Weighting weightings, FlagEncoder encoder, HintsMap hintsMap, Collection<Penalty> penalties) {
        super(encoder);
        this.penalties = penalties;
        this.weightings = weightings;
    }

    @Override
    public double getMinWeight(double distance) {
        return weightings.getMinWeight(distance);
    }

    /**
     * This method calculates the weighting a certain edgeState should be associated. E.g. a high
     * value indicates that the edge should be avoided. Make sure that this method is very fast and
     * optimized as this is called potentially millions of times for one route or a lot more for
     * nearly any preprocessing phase.
     *
     * @param edge             the edge for which the weight should be calculated
     * @param reverse          if the specified edge is specified in reverse direction e.g. from the reverse
     *                         case of a bidirectional search.
     * @param prevOrNextEdgeId if reverse is false this has to be the previous edgeId, if true it
     *                         has to be the next edgeId in the direction from start to end.
     * @return the calculated weight with the specified velocity has to be in the range of 0 and
     * +Infinity. Make sure your method does not return NaN which can e.g. occur for 0/0.
     */
    @Override
    public double calcWeight(EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId) {
        return weightings.calcWeight(edge, reverse, prevOrNextEdgeId) + updateVisitedEdgesAndGetPenalty(edge, reverse, prevOrNextEdgeId);
    }

    @Override
    public long calcMillis(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        final double penalty = updateVisitedEdgesAndGetPenalty(edgeState, reverse, prevOrNextEdgeId) * 1000;
        return (long) (penalty + weightings.calcMillis(edgeState, reverse, prevOrNextEdgeId));
    }

    @Override
    public String getName() {
        return "weighting_with_penalties_" + weightings.getName();
    }


    public static final class WayData {
        public final double firstWayPointLat;
        public final double firstWayPointLng;
        public final double lastWayPointLat;
        public final double lastWayPointLng;

        public WayData(double firstWayPointLat, double firstWayPointLng, double lastWayPointLat, double lastWayPointLng) {
            this.firstWayPointLat = firstWayPointLat;
            this.firstWayPointLng = firstWayPointLng;
            this.lastWayPointLat = lastWayPointLat;
            this.lastWayPointLng = lastWayPointLng;
        }
    }

    protected double updateVisitedEdgesAndGetPenalty(EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId) {
        PointList pointList = edge.fetchWayGeometry(3);
        WayData wayData = new WayData(
                pointList.getLat(0),
                pointList.getLon(0),
                pointList.getLat(pointList.size() - 1),
                pointList.getLon(pointList.size() - 1));
        try {
            visitedEdgesCoordinates.put(edge.getEdge(), wayData);
        } catch (Exception e) {
            logger.error("failed to put, size {} error {}", visitedEdgesCoordinates.size(), e);
            return 0;
        }
        WayData prevWayData;
        if (reverse) { //if reverse is true prevOrNextEdgeId has to be the next edgeId in the direction from start to end.
            prevWayData = wayData;
            wayData = visitedEdgesCoordinates.get(prevOrNextEdgeId);
        } else {
            prevWayData = visitedEdgesCoordinates.get(prevOrNextEdgeId);
        }

        double penaltyCost = .0;
        for (Penalty penalty : penalties) {
            penaltyCost += penalty.getPenalty(edge, reverse, prevOrNextEdgeId, prevWayData, wayData);
        }
        return penaltyCost;
    }
}