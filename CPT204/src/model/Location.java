package model;
import java.util.Comparator;

public class Location {
    private String locationId;
    private double priorityScore;
//    // 建议增加 type 字段，因为任务书提到需要处理不同类型的地点
//    private String type;

    public Location(String locationId, double priorityScore) {
        this.locationId = locationId;
        this.priorityScore = priorityScore;
//        this.type = type;
    }

    public String getLocationId() {
        return locationId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    /**
     * 核心优化：任务书规定的复合排序规则
     * 1. 优先级分数降序 (Priority Score Descending)
     * 2. ID 升序 (Location ID Ascending)
     */
    public static final Comparator<Location> RANKING_COMPARATOR = (l1, l2) -> {
        // 使用 Double.compare 处理浮点数比较，更安全
        if (Double.compare(l2.getPriorityScore(), l1.getPriorityScore()) != 0) {
            return Double.compare(l2.getPriorityScore(), l1.getPriorityScore());
        }
        return l1.getLocationId().compareTo(l2.getLocationId());
    };

    @Override
    public String toString() {
        return String.format("ID: %s | Score: %.2f", locationId, priorityScore);
    }
}