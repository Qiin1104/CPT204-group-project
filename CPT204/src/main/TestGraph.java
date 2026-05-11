package main;

import model.*;
import io.CSVLoader;
import java.util.*;
import algorithm.GraphEngine;
/**
 * 测试类 - 验证 GraphEngine 和 Dijkstra 算法
 */
public class TestGraph {
    public static void main(String[] args) {
        // 1. 加载边的数据
        String pathFile = "CPT204/data/paths.csv";  // 根据实际路径调整
        WeightedGraph graph = CSVLoader.loadWeightedGraph(pathFile);
        System.out.println("Graph loaded successfully!");
        System.out.println("Vertices: " + graph.getVertexCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        // 2. 加载 Task A 选出的 Top 10 位置
        Map<String, List<String>> top10 = CSVLoader.loadTop10Locations();
        List<String> datasetA = top10.get("A");
        List<String> datasetB = top10.get("B");
        List<String> datasetC = top10.get("C");

        System.out.println("\n=== Dataset A Top 10 ===");
        System.out.println(datasetA);
        System.out.println("=== Dataset B Top 10 ===");
        System.out.println(datasetB);
        System.out.println("=== Dataset C Top 10 ===");
        System.out.println(datasetC);

        // 3. 创建图引擎
        GraphEngine engine = new GraphEngine(graph);

        // 4. 计算四个 Case
        System.out.println("\n=== Case 1: A1 to itself ===");
        String a1 = datasetA.get(0);
        PathResult result1 = engine.shortestPath(a1, a1);
        System.out.println("Start: " + a1 + ", End: " + a1);
        System.out.println("Path: " + result1);

        System.out.println("\n=== Case 2: A1 to A10 ===");
        String a10 = datasetA.get(9);
        PathResult result2 = engine.shortestPath(a1, a10);
        if (result2.isReachable()) {
            System.out.println("Start: " + a1 + ", End: " + a10);
            System.out.println("Path: " + result2);
        } else {
            System.out.println("No path found from " + a1 + " to " + a10);
        }


        System.out.println("\n=== Case 3: A1 → B5 → B1 ===");
        String b5 = datasetB.get(4);
        String b1 = datasetB.get(0);
        List<String> waypoints3 = Arrays.asList(b5);
        PathResult result3 = engine.shortestPathWithWaypoints(a1, waypoints3, b1);
        if (result3.isReachable()) {
            System.out.println("Start: " + a1 + ", via: " + b5 + ", End: " + b1);
            System.out.println("Path: " + result3);
        } else {
            System.out.println("No valid path found for Case 3");
        }


        System.out.println("\n=== Case 4: A1 → B5 → C5 → C1 ===");
        String c5 = datasetC.get(4);
        String c1 = datasetC.get(0);
        List<String> waypoints4 = Arrays.asList(b5, c5);
        PathResult result4 = engine.shortestPathWithWaypoints(a1, waypoints4, c1);
        if (result4.isReachable()) {
            System.out.println("Start: " + a1 + ", via: " + b5 + " → " + c5 + ", End: " + c1);
            System.out.println("Path: " + result4);
        } else {
            System.out.println("No valid path found for Case 4");
        }
    }
}