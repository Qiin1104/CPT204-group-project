package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 该类用于存储单个排序算法的性能测试结果。
 * 采用了不可变设计，确保实验数据在生成后不会被修改。
 */
public final class SortResult {
    private final String sorterName;           // 算法名称 (如 "Quick Sort")
    private final long averageNanos;          // 平均运行时间 (单位：纳秒)
    private final List<Location> sortedData;  // 排序后的结果集 (用于获取 Top 10)

    public SortResult(String sorterName, long averageNanos, List<Location> sortedData) {
        this.sorterName = sorterName;
        this.averageNanos = averageNanos;
        // 防御性复制并设为不可变，保证数据完整性
        this.sortedData = Collections.unmodifiableList(new ArrayList<>(sortedData));
    }

    // --- Getter 方法 ---

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