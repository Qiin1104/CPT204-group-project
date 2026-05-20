package algorithm;

import model.Location;
import model.SortResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 该类负责自动化执行排序实验。
 * 职责包括：JVM预热、高频次性能计时、结果验证。
 */
public final class SortPerfomance { // 保持你原代码的拼写，若想修正可改为 SortPerformance
    private final List<Sorter> sorters; // 存储待测试的算法列表（多态的应用）
    private final int repetitions;       // 正式测试重复运行的次数（如 1000 次）
    private final int warmupRepetitions; // JVM 预热运行的次数（建议 10-20 次即可触发 JIT）

    public SortPerfomance(List<Sorter> sorters, int repetitions, int warmupRepetitions) {
        if (sorters == null || sorters.isEmpty()) {
            throw new IllegalArgumentException("必须提供至少一个排序算法。");
        }
        if (repetitions < 1) {
            throw new IllegalArgumentException("正式重复次数必须为正整数。");
        }
        if (warmupRepetitions < 0) {
            throw new IllegalArgumentException("预热重复次数不能为负数。");
        }
        // 使用不可变列表存储算法，体现防御性编程
        this.sorters = Collections.unmodifiableList(new ArrayList<>(sorters));
        this.repetitions = repetitions;
        this.warmupRepetitions = warmupRepetitions;
    }

    /**
     * 运行所有算法并返回结果列表
     * @param data 原始数据集（不会被修改）
     */
    public List<SortResult> run(List<Location> data) {
        List<SortResult> results = new ArrayList<>();

        for (Sorter sorter : sorters) {
            // === 1. JVM 预热阶段 (JVM Warm-up Phase) ===
            // 静默运行算法，不记录时间，目的是触发 JIT 编译并稳定 JVM 状态
            for (int w = 0; w < warmupRepetitions; w++) {
                sorter.sort(data); // 内部自行处理了数据克隆
            }

            // === 2. 正式基准测试阶段 (Official Benchmarking Phase) ===
            long totalNanos = 0L;
            List<Location> lastSortedResult = null;

            for (int i = 0; i < repetitions; i++) {
                // 每次排序前，sorter.sort 内部处理数据克隆，确保实验公平
                long startedAt = System.nanoTime();
                lastSortedResult = sorter.sort(data);
                long finishedAt = System.nanoTime();

                totalNanos += (finishedAt - startedAt);

                // 核心功能：自动化验证排序结果是否符合规则
                validateSorted(lastSortedResult, sorter.name());
            }

            // 计算 1000 次运行的平均耗时并封装成结果对象
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