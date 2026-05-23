package algorithm;
import model.Location;

import java.util.ArrayList;
import java.util.List;

public final class BubbleSorter implements Sorter{
    @Override
    public String name() {
        return "Bubble Sort";
    }

    @Override
    public List<Location> sort(List<Location> data) {
        // Make a copy of the data to avoid modifying the original dataset and ensure a fair experiment.
        List<Location> sorted = new ArrayList<>(data);
        int n = sorted.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false; //Check if any swaps occurred in this round.
            for (int j = 0; j < n - 1 - i; j++) {
                if (Location.RANKING_COMPARATOR.compare(sorted.get(j), sorted.get(j + 1)) > 0) {
                    Location temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                    swapped = true;
                }
            }
            //If no swaps occur in a round, it means the array is already sorted, so we can stop early
            if (!swapped) break;
        }
        return sorted;
    }
}
