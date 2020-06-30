package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.util.PMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SupportTollRoadsFlagEncoderTest {
    private final PMap pMap = new PMap();
    private final SupportTollRoadsFlagEncoder supportTollRoadsFlagEncoder = new SupportTollRoadsFlagEncoder(pMap);
    private final ReaderWay readerWay = new ReaderWay(1);
    private final ReaderWay readerWayWithoutTollBit = new ReaderWay(2);

    @Before
    public void defineWayBits() {
        readerWay.setTag("toll", String.valueOf(Boolean.TRUE));
        readerWay.setTag("highway", "motorroad");
        readerWayWithoutTollBit.setTag("toll", String.valueOf(Boolean.FALSE));
        readerWayWithoutTollBit.setTag("highway", "motorroad");
    }

    @Test
    public void notAccept() {
        readerWay.setTag("toll", String.valueOf(Boolean.TRUE));
        assertEquals(supportTollRoadsFlagEncoder.getAccess(readerWay), EncodingManager.Access.CAN_SKIP);

        readerWay.setTag("toll", "yes");
        assertEquals(supportTollRoadsFlagEncoder.getAccess(readerWay), EncodingManager.Access.CAN_SKIP);
        readerWay.removeTag("toll");


        readerWay.setTag("barrier", "toll_booth");
        assertEquals(supportTollRoadsFlagEncoder.getAccess(readerWay), EncodingManager.Access.CAN_SKIP);
        readerWay.removeTag("barrier");
    }

    @Test
    public void accept() {
        assertNotEquals(supportTollRoadsFlagEncoder.getAccess(readerWayWithoutTollBit), EncodingManager.Access.CAN_SKIP);
    }

}