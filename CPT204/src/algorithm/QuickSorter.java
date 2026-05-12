package algorithm;
import model.Location;

import java.util.ArrayList;
import java.util.List;

public final class QuickSorter implements Sorter {
    @Override
    public String name() {
        return "Quick Sort";
    }

    @Override
    public List<Location> sort(List<Location> data) {
        List<Location> sorted = new ArrayList<>(data);
        if (sorted.size() > 0) {
            quickSort(sorted, 0, sorted.size() - 1);
        }
        return sorted;
    }

    private void quickSort(List<Location> data, int low, int high) {
        if (low >= high) return;

        int left = low, right = high;
        // 选择中间元素作为 Pivot，提高在有序数据集上的稳定性
        Location pivot = data.get(low + (high - low) / 2);

        while (left <= right) {
            while (Location.RANKING_COMPARATOR.compare(data.get(left), pivot) < 0) left++;
            while (Location.RANKING_COMPARATOR.compare(data.get(right), pivot) > 0) right--;

            if (left <= right) {
                Location temp = data.get(left);
                data.set(left, data.get(right));
                data.set(right, temp);
                left++;
                right--;
            }
        }

        if (low < right) quickSort(data, low, right);
        if (left < high) quickSort(data, left, high);
    }
}

