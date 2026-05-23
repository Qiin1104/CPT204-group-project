package algorithm;
import model.Location;
import java.util.ArrayList;
import java.util.List;
public final class MergeSorter implements Sorter{
    @Override
    public String name() {
        return "Merge Sort";
    }

    @Override
    public List<Location> sort(List<Location> data) {
        List<Location> sorted = new ArrayList<>(data);
        if (sorted.size() < 2) return sorted;

        //Pre-create a buffer to reduce memory usage.
        List<Location> buffer = new ArrayList<>(sorted);
        mergeSort(sorted, buffer, 0, sorted.size());
        return sorted;
    }

    private void mergeSort(List<Location> data, List<Location> buffer, int start, int end) {
        if (end - start <= 1) return;

        int mid = start + (end - start) / 2;
        mergeSort(data, buffer, start, mid);
        mergeSort(data, buffer, mid, end);
        merge(data, buffer, start, mid, end);
    }

    private void merge(List<Location> data, List<Location> buffer, int start, int mid, int end) {
        int left = start, right = mid, write = start;

        while (left < mid && right < end) {
            if (Location.RANKING_COMPARATOR.compare(data.get(left), data.get(right)) <= 0) {
                buffer.set(write++, data.get(left++));
            } else {
                buffer.set(write++, data.get(right++));
            }
        }
        while (left < mid) buffer.set(write++, data.get(left++));
        while (right < end) buffer.set(write++, data.get(right++));

        for (int i = start; i < end; i++) data.set(i, buffer.get(i));
    }
}

