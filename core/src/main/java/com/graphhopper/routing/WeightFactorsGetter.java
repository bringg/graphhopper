package com.graphhopper.routing;

import com.graphhopper.util.EdgeIteratorState;

public interface WeightFactorsGetter {
    double getFactor(EdgeIteratorState edgeState, boolean reverse);
}
