package com.graphhopper.routing.weighting;

import com.graphhopper.routing.WeightFactorsGetter;
import com.graphhopper.routing.util.EdgeData;
import com.graphhopper.util.EdgeIteratorState;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightingsWithFactorsTest {
    private final Random random = new Random();

    @Test
    public void calcWeight() {
        int edgeId = random.nextInt(), baseNode = random.nextInt(), adjNode = random.nextInt();
        double weight = 100 + random.nextDouble();
        final double factor =  1 - random.nextDouble();

        WeightFactorsGetter weightFactorsGetter = new WeightFactorsGetter() {
            @Override
            public double getFactor(EdgeIteratorState edgeState, boolean reverse) {
                return !reverse ? factor : 1;
            }
        };

        EdgeIteratorState edgeIteratorState = mock(EdgeIteratorState.class);
        Weighting weighting = mock(Weighting.class);

        when(edgeIteratorState.getEdge()).thenReturn(edgeId);
        when(edgeIteratorState.getBaseNode()).thenReturn(baseNode);
        when(edgeIteratorState.getAdjNode()).thenReturn(adjNode);
        when(weighting.calcWeight(edgeIteratorState, false, 1)).thenReturn(weight);
        when(weighting.calcWeight(edgeIteratorState, true, 1)).thenReturn(weight);

        final WeightingsWithFactors weightingsWithFactors = new WeightingsWithFactors(weighting, weightFactorsGetter);
        assertEquals(weightingsWithFactors.calcWeight(edgeIteratorState, false, 1), weight * factor, .001);
        assertEquals(weightingsWithFactors.calcWeight(edgeIteratorState, true, 1), weight, .001);
    }

    @Test
    public void calcWeightReverse() {
        int edgeId = random.nextInt(), baseNode = random.nextInt(), adjNode = random.nextInt();
        double weight = random.nextDouble();
        final double factor = random.nextDouble();
        WeightFactorsGetter weightFactorsGetter = new WeightFactorsGetter() {
            @Override
            public double getFactor(EdgeIteratorState edgeState, boolean reverse) {
                return reverse ? factor : 1;
            }
        };

        EdgeIteratorState edgeIteratorState = mock(EdgeIteratorState.class);
        Weighting weighting = mock(Weighting.class);

        when(edgeIteratorState.getEdge()).thenReturn(edgeId);
        when(edgeIteratorState.getBaseNode()).thenReturn(baseNode);
        when(edgeIteratorState.getAdjNode()).thenReturn(adjNode);
        when(weighting.calcWeight(edgeIteratorState, true, 1)).thenReturn(weight);
        when(weighting.calcWeight(edgeIteratorState, false, 1)).thenReturn(weight);

        final WeightingsWithFactors weightingsWithFactors = new WeightingsWithFactors(weighting, weightFactorsGetter);

        assertEquals(weightingsWithFactors.calcWeight(edgeIteratorState, true, 1), weight * factor, .001);
        assertEquals(weightingsWithFactors.calcWeight(edgeIteratorState, false, 1), weight, .001);
    }
}