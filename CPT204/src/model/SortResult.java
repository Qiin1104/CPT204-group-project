package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * This class is used to store the performance test results of a single sorting algorithm.
 * It adopts an immutable design to ensure that experimental data cannot be modified after generation.
 */
public final class SortResult {
    private final String sorterName;
    private final long averageNanos;
    private final List<Location> sortedData;

    public SortResult(String sorterName, long averageNanos, List<Location> sortedData) {
        this.sorterName = sorterName;
        this.averageNanos = averageNanos;
        this.sortedData = Collections.unmodifiableList(new ArrayList<>(sortedData));
    }

    // --- Getter Methods ---

    public String getSorterName() {
        return sorterName;
    }

    public long getAverageNanos() {
        return averageNanos;
    }

    public List<Location> getSortedData() {
        return sortedData;
    }
}