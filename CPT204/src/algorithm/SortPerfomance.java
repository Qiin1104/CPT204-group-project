package algorithm;

import model.Location;
import model.SortResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles the automation of sorting experiments.
 * Key functions: JVM warmup, high-frequency performance benchmarking, and result verification.
 */
public final class SortPerfomance {
    private final List<Sorter> sorters;  //Store the list of algorithms to be tested (application of polymorphism).
    private final int repetitions;       // Number of repetitions for the formal test (e.g., 1000 times)
    private final int warmupRepetitions; // // JVM warmup iterations (suggested 10-20 to trigger JIT)

    public SortPerfomance(List<Sorter> sorters, int repetitions, int warmupRepetitions) {
        if (sorters == null || sorters.isEmpty()) {
            throw new IllegalArgumentException("At least one sorting algorithm must be provided");
        }
        if (repetitions < 1) {
            throw new IllegalArgumentException("The number of formal repetitions must be a positive integer");
        }
        if (warmupRepetitions < 0) {
            throw new IllegalArgumentException("The number of warmup repetitions cannot be negative");
        }

        this.sorters = Collections.unmodifiableList(new ArrayList<>(sorters));
        this.repetitions = repetitions;
        this.warmupRepetitions = warmupRepetitions;
    }

    /**
     * Run all algorithms and return a list of results.
     * @param data The original dataset (will not be modified)
     */
    public List<SortResult> run(List<Location> data) {
        List<SortResult> results = new ArrayList<>();

        for (Sorter sorter : sorters) {

            for (int w = 0; w < warmupRepetitions; w++) {
                sorter.sort(data); // 内部自行处理了数据克隆
            }


            long totalNanos = 0L;
            List<Location> lastSortedResult = null;

            for (int i = 0; i < repetitions; i++) {

                long startedAt = System.nanoTime();
                lastSortedResult = sorter.sort(data);
                long finishedAt = System.nanoTime();

                totalNanos += (finishedAt - startedAt);

                // Core functionality: Automatically verify if the sorting results comply with the rules.
                validateSorted(lastSortedResult, sorter.name());
            }

            // Calculate the average time of 1000 runs and wrap it into a result object.
            long averageTime = totalNanos / repetitions;
            results.add(new SortResult(sorter.name(), averageTime, lastSortedResult));
        }
        return results;
    }

    /**
     * Check if the sorting is correct: traverse the list to ensure that each preceding element >= the following element (according to RANKING_COMPARATOR rules).
     */
    private void validateSorted(List<Location> data, String sorterName) {
        for (int i = 1; i < data.size(); i++) {
            // Validate using the unified comparison rules defined in Location.
            if (Location.RANKING_COMPARATOR.compare(data.get(i - 1), data.get(i)) > 0) {
                throw new IllegalStateException(sorterName + " index " + i + " have error。");
            }
        }
    }
}