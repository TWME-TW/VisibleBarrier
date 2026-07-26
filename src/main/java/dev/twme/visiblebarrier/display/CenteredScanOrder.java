package dev.twme.visiblebarrier.display;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class CenteredScanOrder {
    private CenteredScanOrder() {
    }

    static List<Offset> create(int horizontalRadius, int verticalRadius) {
        int horizontalDiameter = horizontalRadius * 2 + 1;
        int verticalDiameter = verticalRadius * 2 + 1;
        List<Offset> offsets = new ArrayList<>(horizontalDiameter * horizontalDiameter * verticalDiameter);

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    offsets.add(new Offset(x, y, z));
                }
            }
        }

        offsets.sort(Comparator.comparingInt(Offset::distanceSquared));
        return List.copyOf(offsets);
    }

    record Offset(int x, int y, int z) {
        int distanceSquared() {
            return x * x + y * y + z * z;
        }
    }
}
