package com.graphhopper.routing.weighting;

import com.graphhopper.routing.VirtualEdgeIteratorState;
import com.graphhopper.routing.profiles.EncodedValue;
import com.graphhopper.routing.profiles.EnumEncodedValue;
import com.graphhopper.routing.profiles.Toll;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.GHUtility;
import com.graphhopper.util.Helper;
import com.graphhopper.util.PMap;
import com.graphhopper.util.Parameters;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FastestWeightingWithTollRoadsPenaltiesTest {
    EncodingManager encodingManager = spy(EncodingManager.create("car"));
    private final FlagEncoder encoder = spy(encodingManager.getEncoder("car"));
    private final Random random = new Random();


    @Test
    public void testWeightWrongHeading() {
        final int headingPenalty = 100 + random.nextInt(200);
        final int tollFactor = 100 + random.nextInt(200);
        final EnumEncodedValue tollEncoder = new EnumEncodedValue("toll", Toll.class);
        tollEncoder.init(new EncodedValue.InitializerConfig());
        doReturn(tollEncoder).when(encoder).getEnumEncodedValue(ArgumentMatchers.eq(Toll.KEY), ArgumentMatchers.eq(Toll.class));
        FastestWeightingWithTollRoadsPenalties instance = spy(new FastestWeightingWithTollRoadsPenalties(encoder, new PMap().
                put(Parameters.Routing.HEADING_PENALTY, headingPenalty), tollFactor));

        VirtualEdgeIteratorState virtEdge = new VirtualEdgeIteratorState(0, 1, 1, 2, 10,
                GHUtility.setProperties(encodingManager.createEdgeFlags(), encoder, 10, true, false), "test", Helper.createPointList(51, 0, 51, 1), false);

        doReturn(tollFactor).when(instance).getFactor(ArgumentMatchers.eq(virtEdge), ArgumentMatchers.eq(false));
        doReturn(1).when(instance).getFactor(ArgumentMatchers.eq(virtEdge), ArgumentMatchers.eq(true));
        double time = instance.calcWeight(virtEdge, false, 0);

        virtEdge.setUnfavored(true);
        // heading penalty on edge
        assertEquals(time + headingPenalty * tollFactor, instance.calcWeight(virtEdge, false, 0), 1e-8);
        // only after setting it
        virtEdge.setUnfavored(true);
        assertEquals(time/tollFactor + headingPenalty, instance.calcWeight(virtEdge, true, 0), 1e-8);
        // but not after releasing it
        virtEdge.setUnfavored(false);
        assertEquals(time, instance.calcWeight(virtEdge, false, 0), 1e-8);

        // test default penalty
        virtEdge.setUnfavored(true);
        instance = spy(new FastestWeightingWithTollRoadsPenalties(encoder, new PMap(), tollFactor));
        doReturn(tollFactor).when(instance).getFactor(ArgumentMatchers.eq(virtEdge), ArgumentMatchers.eq(false));
        doReturn(1).when(instance).getFactor(ArgumentMatchers.eq(virtEdge), ArgumentMatchers.eq(true));
        assertEquals(time + tollFactor * Parameters.Routing.DEFAULT_HEADING_PENALTY, instance.calcWeight(virtEdge, false, 0), 1e-8);
        assertEquals(time/ tollFactor + Parameters.Routing.DEFAULT_HEADING_PENALTY, instance.calcWeight(virtEdge, true, 0), 1e-8);
    }
}