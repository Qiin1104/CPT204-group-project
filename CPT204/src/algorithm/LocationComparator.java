package algorithm;

import model.Location;
import java.util.Comparator;

public class LocationComparator implements Comparator<Location> {

    @Override
    public int compare(Location l1, Location l2) {
        // 1. 优先级降序 (Descending)
        if (l1.getPriorityScore() != l2.getPriorityScore()) {
            return Double.compare(l2.getPriorityScore(), l1.getPriorityScore());
        }
        // 2. ID 升序 (Ascending) - 当优先级分值相同时
        return l1.getLocationId().compareTo(l2.getLocationId());
    }
}