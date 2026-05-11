package model;

import java.util.*;

/**
 * 加权图类 - 使用邻接表存储
 * 适用于 Dijkstra 最短路径算法
 */
public class WeightedGraph {
    // 邻接表：每个顶点 → 它的邻居边列表
    private Map<String, List<WeightedEdge>> adjacencyList;
    private int edgeCount;      // 边数


    public WeightedGraph() {
        this.adjacencyList = new HashMap<>();
        this.edgeCount = 0;
    }

    /**
     * 添加一条边（无向图，会添加两条方向）
     */
    public void addEdge(String from, String to, double weight) {
        // 添加 from → to
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new WeightedEdge(from, to, weight));
        // 添加 to → from（无向图）
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>())
                .add(new WeightedEdge(to, from, weight));
        edgeCount++;
    }


    /**
     * 获取某个顶点的所有邻居边
     */
    public List<WeightedEdge> getNeighbors(String vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    /**
     * 获取所有顶点集合
     */
    public Set<String> getAllVertices() {
        return adjacencyList.keySet();
    }

    /**
     * 检查顶点是否存在于图中
     */
    public boolean containsVertex(String vertex) {
        return adjacencyList.containsKey(vertex);
    }

    /**
     * 获取顶点数量
     */
    public int getVertexCount() {
        return adjacencyList.size();  // Map 的大小就是顶点数
    }

    /**
     * 获取边数量
     */
    public int getEdgeCount() {
        return edgeCount;
    }

    /**
     * 打印图的基本信息（用于调试）
     */
    public void printGraph() {
        System.out.println("=== Weighted Graph ===");
        System.out.println("Vertices: " + adjacencyList.size());
        System.out.println("Edges: " + edgeCount);
        for (Map.Entry<String, List<WeightedEdge>> entry : adjacencyList.entrySet()) {
            System.out.print(entry.getKey() + " → ");
            for (WeightedEdge e : entry.getValue()) {
                System.out.print(e.getTo() + "(" + e.getWeight() + ") ");
            }
            System.out.println();
        }
    }
}