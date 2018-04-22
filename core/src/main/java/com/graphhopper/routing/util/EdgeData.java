package com.graphhopper.routing.util;


import java.util.Objects;

public class EdgeData {
    private final int edgeId;
    private final boolean reverse;

    public EdgeData(int edgeId, boolean reverse) {
        this.edgeId = edgeId;
        this.reverse = reverse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return edgeId == edgeData.edgeId &&
                reverse == edgeData.reverse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeId, reverse);
    }
}