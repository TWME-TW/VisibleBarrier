package dev.twme.visiblebarrier.display;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class CenteredScanOrderTest {
    @Test
    void includesEveryOffsetOnceFromNearestToFarthest() {
        int horizontalRadius = 3;
        int verticalRadius = 2;

        List<CenteredScanOrder.Offset> offsets = CenteredScanOrder.create(horizontalRadius, verticalRadius);

        assertEquals(245, offsets.size());
        assertEquals(new CenteredScanOrder.Offset(0, 0, 0), offsets.getFirst());

        Set<CenteredScanOrder.Offset> uniqueOffsets = new HashSet<>(offsets);
        assertEquals(offsets.size(), uniqueOffsets.size());

        int previousDistanceSquared = -1;
        for (CenteredScanOrder.Offset offset : offsets) {
            assertTrue(Math.abs(offset.x()) <= horizontalRadius);
            assertTrue(Math.abs(offset.y()) <= verticalRadius);
            assertTrue(Math.abs(offset.z()) <= horizontalRadius);
            assertTrue(offset.distanceSquared() >= previousDistanceSquared);
            previousDistanceSquared = offset.distanceSquared();
        }
    }
}
