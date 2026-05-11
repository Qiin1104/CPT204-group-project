package algorithm;

import model.PathResult;
import model.WeightedEdge;
import model.WeightedGraph;

import java.util.*;

/**
 * 图引擎 - 实现 Dijkstra 最短路径算法
 */
public class GraphEngine {
    private WeightedGraph graph;

    public GraphEngine(WeightedGraph graph) {
        this.graph = graph;
    }

    /**
     * Dijkstra 算法 - 计算从起点到终点的最短路径
     * @param start 起点ID
     * @param end 终点ID
     * @return PathResult 包含路径和总距离
     */
    public PathResult shortestPath(String start, String end) {
        // 检查起点和终点是否在图中
        if (!graph.containsVertex(start)) {
            System.err.println("Start vertex not in graph: " + start);
            return PathResult.unreachable();
        }
        if (!graph.containsVertex(end)) {
            System.err.println("End vertex not in graph: " + end);
            return PathResult.unreachable();
        }
        // 起点等于终点的情况
        if (start.equals(end)) {
            List<String> path = new ArrayList<>();
            path.add(start);
            return new PathResult(path, 0.0);
        }

        // 距离映射：顶点 → 从起点到该顶点的最短距离
        Map<String, Double> distance = new HashMap<>();
        // 前驱映射：顶点 → 最短路径中的上一个顶点
        Map<String, String> previous = new HashMap<>();
        // 优先队列：按距离排序，存储 (distance, vertex)
        PriorityQueue<Map.Entry<Double, String>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getKey)
        );
        // 已访问的顶点集合
        Set<String> visited = new HashSet<>();

        // 初始化
        for (String vertex : graph.getAllVertices()) {
            distance.put(vertex, Double.POSITIVE_INFINITY);
            previous.put(vertex, null);
        }
        distance.put(start, 0.0);
        pq.offer(new AbstractMap.SimpleEntry<>(0.0, start));

        while (!pq.isEmpty()) {
            Map.Entry<Double, String> entry = pq.poll();
            String current = entry.getValue();
            double currentDist = entry.getKey();

            // 如果当前顶点已访问，跳过（懒惰删除）
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // 如果到达终点，提前结束
            if (current.equals(end)) {
                break;
            }

            // 遍历当前顶点的所有邻居
            for (WeightedEdge edge : graph.getNeighbors(current)) {
                String neighbor = edge.getTo();
                double weight = edge.getWeight();

                if (visited.contains(neighbor)) {
                    continue;
                }

                double newDist = currentDist + weight;
                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    pq.offer(new AbstractMap.SimpleEntry<>(newDist, neighbor));
                }
            }
        }

        // 检查是否找到路径
        if (distance.get(end) == Double.POSITIVE_INFINITY) {
            System.err.println("No path found from " + start + " to " + end);
            return PathResult.unreachable();
        }
        // 构建路径
        List<String> path = reconstructPath(start, end, previous);

        // 如果路径构建失败（比如起点不匹配），返回不可达
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return PathResult.unreachable();
        }

        double totalDistance = distance.get(end);

        return new PathResult(path, totalDistance);
    }

    /**
     * 从 previous 映射中重构路径
     */
    private List<String> reconstructPath(String start, String end, Map<String, String> previous) {
        List<String> path = new ArrayList<>();
        String current = end;

        // 如果无法到达终点，返回空路径
        if (previous.get(end) == null && !start.equals(end)) {
            return path;
        }

        // 从终点回溯到起点
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        // 反转得到从起点到终点的顺序
        Collections.reverse(path);

        // 验证起点是否正确
        if (!path.isEmpty() && !path.get(0).equals(start)) {
            return new ArrayList<>();
        }

        return path;
    }

    /**
     * 计算带必经点的最短路径
     * @param start 起点
     * @param waypoints 必经点列表（按顺序经过）
     * @param end 终点
     * @return PathResult 完整路径
     */
    public PathResult shortestPathWithWaypoints(String start, List<String> waypoints, String end) {
        List<String> fullPath = new ArrayList<>();
        double totalDistance = 0.0;
        String currentStart = start;

        // 逐段计算
        for (String waypoint : waypoints) {
            PathResult segment = shortestPath(currentStart, waypoint);
            if (!segment.isReachable()) {
                System.err.println("Cannot reach waypoint " + waypoint + " from " + currentStart);
                return PathResult.unreachable();
            }
            // 拼接路径（跳过重复的起点）
            if (fullPath.isEmpty()) {
                fullPath.addAll(segment.getPath());
            } else {
                List<String> segmentPath = segment.getPath();
                // 确保有路径可拼接
                if (segmentPath.size() <= 1) {
                    return PathResult.unreachable();
                }
                fullPath.addAll(segmentPath.subList(1, segmentPath.size()));
            }
            totalDistance += segment.getTotalDistance();
            currentStart = waypoint;
        }

        // 最后一段
        PathResult lastSegment = shortestPath(currentStart, end);
        if (!lastSegment.isReachable()) {
            System.err.println("Cannot reach end " + end + " from " + currentStart);
            return PathResult.unreachable();
        }

        // 拼接最后一段
        List<String> lastPath = lastSegment.getPath();
        if (lastPath.size() <= 1) {
            return PathResult.unreachable();
        }
        fullPath.addAll(lastPath.subList(1, lastPath.size()));
        totalDistance += lastSegment.getTotalDistance();

        return new PathResult(fullPath, totalDistance);
    }
}