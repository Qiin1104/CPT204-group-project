package model;
import java.util.Comparator;

public class Location {
    private String locationId;
    private double priorityScore;


    public Location(String locationId, double priorityScore) {
        this.locationId = locationId;
        this.priorityScore = priorityScore;

    }

    public String getLocationId() {
        return locationId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public static final Comparator<Location> RANKING_COMPARATOR = (l1, l2) -> {
        if (Double.compare(l2.getPriorityScore(), l1.getPriorityScore()) != 0) {
            return Double.compare(l2.getPriorityScore(), l1.getPriorityScore());
        }
        return l1.getLocationId().compareTo(l2.getLocationId());
    };

    @Override
    public String toString() {
        return String.format("ID: %s | Score: %.2f", locationId, priorityScore);
    }
}