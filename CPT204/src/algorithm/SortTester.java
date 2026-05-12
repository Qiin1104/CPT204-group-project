package algorithm;
import model.Location;
import model.SortResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 该类负责自动化执行排序实验。
 * 职责包括：数据备份、性能计时、结果验证。
 */
public final class SortTester {
    private final List<Sorter> sorters; // 存储待测试的算法列表（多态的应用）
    private final int repetitions;      // 每个算法重复运行的次数（用于取平均值）

    public SortTester(List<Sorter> sorters, int repetitions) {
        if (sorters == null || sorters.isEmpty()) {
            throw new IllegalArgumentException("必须提供至少一个排序算法。");
        }
        if (repetitions < 1) {
            throw new IllegalArgumentException("重复次数必须为正整数。");
        }
        // 使用不可变列表存储算法，体现防御性编程
        this.sorters = Collections.unmodifiableList(new ArrayList<>(sorters));
        this.repetitions = repetitions;
    }

    /**
     * 运行所有算法并返回结果列表
     * @param data 原始数据集（不会被修改）
     */
    public List<SortResult> run(List<Location> data) {
        List<SortResult> results = new ArrayList<>();

        for (Sorter sorter : sorters) {
            long totalNanos = 0L;
            List<Location> lastSortedResult = null;

            for (int i = 0; i < repetitions; i++) {
                // 每次排序前，sorter.sort 内部应处理数据克隆
                long startedAt = System.nanoTime();
                lastSortedResult = sorter.sort(data);
                long finishedAt = System.nanoTime();

                totalNanos += (finishedAt - startedAt);

                // 核心功能：自动化验证排序结果是否符合规则
                validateSorted(lastSortedResult, sorter.name());
            }

            // 计算平均耗时并封装成结果对象
            long averageTime = totalNanos / repetitions;
            results.add(new SortResult(sorter.name(), averageTime, lastSortedResult));
        }
        return results;
    }

    /**
     * 检测排序是否正确：遍历列表，确保前一个元素 >= 后一个元素（按 RANKING_COMPARATOR 规则）
     */
    private void validateSorted(List<Location> data, String sorterName) {
        for (int i = 1; i < data.size(); i++) {
            // 使用 Location 中定义的统一比较规则进行校验
            if (Location.RANKING_COMPARATOR.compare(data.get(i - 1), data.get(i)) > 0) {
                throw new IllegalStateException(sorterName + " 在索引 " + i + " 处产生错误排序。");
            }
        }
    }
}