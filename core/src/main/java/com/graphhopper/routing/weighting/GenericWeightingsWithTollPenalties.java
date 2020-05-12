package com.graphhopper.routing.weighting;

import com.graphhopper.Penalties.Penalty;
import com.graphhopper.routing.profiles.EnumEncodedValue;
import com.graphhopper.routing.profiles.Toll;
import com.graphhopper.routing.util.DataFlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

import java.util.Collection;

public class GenericWeightingsWithTollPenalties extends GenericWeighting {
    private final EnumEncodedValue<Toll> tollEnc = new EnumEncodedValue<>(Toll.KEY, Toll.class, true);

    public GenericWeightingsWithTollPenalties(DataFlagEncoder encoder, PMap hintsMap) {
        super(encoder, hintsMap);
    }


    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        final Toll toll = tollEnc.getEnum(reverse, edgeState.getFlags());
        if (toll == Toll.NO)
            return super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        return super.calcWeight(edgeState, reverse, prevOrNextEdgeId)*10;
    }

    @Override
    public String getName() {
        return "generic_with_toll_roads";
    }
}
