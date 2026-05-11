package algorithm;

import model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingEngine {

    // Bubble Sort
    public static void bubbleSort(List<Location> list, Comparator<Location> c) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (c.compare(list.get(j), list.get(j + 1)) > 0) {
                    Collections.swap(list, j, j + 1);
                }
            }
        }
    }

    // Quick Sort (这里只写入口)
    public static void quickSort(List<Location> list, int low, int high, Comparator<Location> c) {
        if (low < high) {
            int pi = partition(list, low, high, c);
            quickSort(list, low, pi - 1, c);
            quickSort(list, pi + 1, high, c);
        }
    }

    private static int partition(List<Location> list, int low, int high, Comparator<Location> c) {
        Location pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (c.compare(list.get(j), pivot) < 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // Merge Sort (这里只写入口)
    public static void mergeSort(List<Location> list, Comparator<Location> c) {
        if (list.size() < 2) return;
        int mid = list.size() / 2;
        List<Location> left = new ArrayList<>(list.subList(0, mid));
        List<Location> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, c);
        mergeSort(right, c);
        merge(list, left, right, c);
    }

    private static void merge(List<Location> list, List<Location> left, List<Location> right, Comparator<Location> c) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (c.compare(left.get(i), right.get(j)) <= 0) list.set(k++, left.get(i++));
            else list.set(k++, right.get(j++));
        }
        while (i < left.size()) list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }
}
