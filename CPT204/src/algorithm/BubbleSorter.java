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
        // 复制一份数据，避免修改原始数据集，确保实验公平 [cite: 113]
        List<Location> sorted = new ArrayList<>(data);
        int n = sorted.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false; // 优化：检查本轮是否有交换
            for (int j = 0; j < n - 1 - i; j++) {
                // 使用任务书规定的排序规则进行比较
                if (Location.RANKING_COMPARATOR.compare(sorted.get(j), sorted.get(j + 1)) > 0) {
                    Location temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                    swapped = true;
                }
            }
            // 如果某一轮没有交换，说明已经有序，提前停止（这是性能分析的加分点）
            if (!swapped) break;
        }
        return sorted;
    }
}
