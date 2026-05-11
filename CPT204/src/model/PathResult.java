package model;
import java.util.ArrayList;
import java.util.List;

/**
 * 路径结果类 - 存储 Dijkstra 的计算结果
 */
public class PathResult {
    private List<String> path;      // 路径节点列表（起点 → 终点）
    private double totalDistance;   // 总距离
    private boolean reachable;  // 标记是否可达,即路径是否存在（路径非空且有合法距离）

    public PathResult() {
        this.path = new ArrayList<>();
        this.totalDistance = 0.0;
        this.reachable = false;
    }

    public PathResult(List<String> path, double totalDistance) {
        this.path = new ArrayList<>(path);
        this.totalDistance = totalDistance;
        this.reachable = !path.isEmpty() && totalDistance < Double.POSITIVE_INFINITY;
    }

    // 专门用于不可达的情况
    public static PathResult unreachable() {
        PathResult result = new PathResult();
        result.reachable = false;
        return result;
    }

    public List<String> getPath() { return path; }
    public double getTotalDistance() { return totalDistance; }
    public boolean isReachable() { return reachable; }

    public void setPath(List<String> path) { this.path = new ArrayList<>(path); }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }


    @Override
    public String toString() {
        if (!reachable) {
            return "No path found (vertices are not connected)";
        }
        if (path.isEmpty()) {
            return "Empty path";
        }
        return String.join(" → ", path) + " (Distance: " + totalDistance + ")";
    }
}