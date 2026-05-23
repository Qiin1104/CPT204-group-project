package model;
import java.util.ArrayList;
import java.util.List;

public class PathResult {
    private List<String> path;      // List of nodes representing the path (Start → End)
    private double totalDistance;   // Total distance of the path
    private boolean reachable;  // Flag indicating if the path exists (true if path is non-empty and valid)

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