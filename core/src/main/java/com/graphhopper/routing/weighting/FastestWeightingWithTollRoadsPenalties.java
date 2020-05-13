package com.graphhopper.routing.weighting;

import com.graphhopper.routing.profiles.EnumEncodedValue;
import com.graphhopper.routing.profiles.Toll;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

public class FastestWeightingWithTollRoadsPenalties extends FastestWeighting {
    private final EnumEncodedValue<Toll> tollEnc;
    private final int tollRoadPenalty;

    public FastestWeightingWithTollRoadsPenalties(FlagEncoder encoder, PMap map, int tollRoadPenalty) {
        super(encoder, map);
        this.tollEnc = encoder.getEnumEncodedValue(Toll.KEY, Toll.class);
        this.tollRoadPenalty = tollRoadPenalty;
    }

    @Override
    public double calcWeight(EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId) {
        return super.calcWeight(edge, reverse, prevOrNextEdgeId) * getFactor(edge, reverse);
    }

    @Override
    public String getName() {
        return "fastest_with_toll_road_weights";
    }

    int getFactor(EdgeIteratorState edge, boolean reverse) {
        return tollEnc.getEnum(reverse, edge.getFlags()) == Toll.ALL ?
             tollRoadPenalty : 1;
    }
}
