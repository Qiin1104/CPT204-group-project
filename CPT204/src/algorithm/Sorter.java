package algorithm;
import model.Location;

import java.util.List;
/**
 * Defines the common interface for sorting algorithms.
 */
public interface Sorter {
    // Return the algorithm name for easy table output in the Task A report.
    String name();

    // Sorting method that takes the original list and returns a new sorted list.
    List<Location> sort(List<Location> data);
}
