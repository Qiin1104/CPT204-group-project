package main;

import model.Location;
import model.SortResult;
import model.WeightedGraph;
import model.PathResult;
import io.CSVLoader;
import algorithm.*;
import visualization.PathVisualizer;

import java.io.IOException;
import java.util.*;

/**
 * 项目统一入口 - 同时执行 Task A 和 Task B
 */
public class main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         CPT204 Group Project - Urban Infrastructure            ║");
        System.out.println("║              Inspection System (Task A + Task B)               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        // ==================== Task A ====================
        System.out.println("\n\n==================== TASK A: Sorting Algorithms ====================");
        Map<String, List<String>> inspectionResults = runTaskA();

        if (inspectionResults == null || inspectionResults.isEmpty()) {
            System.err.println("Task A failed to produce results. Exiting.");
            return;
        }

        // ==================== Task B ====================
        System.out.println("\n\n==================== TASK B: Shortest Path ====================");

        List<String> datasetA = inspectionResults.get("A");
        List<String> datasetB = inspectionResults.get("B");
        List<String> datasetC = inspectionResults.get("C");

        runTaskB(datasetA, datasetB, datasetC);
    }


    // ==================== Task A 实现 ====================

    private static Map<String, List<String>> runTaskA() {
        String[] filePaths = {
                "CPT204/data/candidates_A.csv",
                "CPT204/data/candidates_B.csv",
                "CPT204/data/candidates_C.csv"
        };

        List<Sorter> sorters = Arrays.asList(
                new BubbleSorter(),
                new QuickSorter(),
                new MergeSorter()
        );

        SortPerfomance benchmark = new SortPerfomance(sorters, 3);
        Map<String, List<String>> inspectionResults = new LinkedHashMap<>();

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s | %s\n",
                "Dataset", "Algorithm", "Avg Time (ns)", "Top 10 Selected Locations");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String path : filePaths) {
            String datasetName = path.substring(path.lastIndexOf("_") + 1, path.lastIndexOf("."));
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData == null || originalData.isEmpty()) continue;

            List<SortResult> reports = benchmark.run(originalData);

            if (!reports.isEmpty()) {
                List<Location> fullSortedList = reports.get(0).getSortedData();
                List<String> top10Ids = new ArrayList<>();
                for (int i = 0; i < 10 && i < fullSortedList.size(); i++) {
                    top10Ids.add(fullSortedList.get(i).getLocationId());
                }
                inspectionResults.put(datasetName, top10Ids);
            }

            for (SortResult report : reports) {
                List<Location> sortedList = report.getSortedData();
                List<String> top10Ids = new ArrayList<>();
                for (int i = 0; i < 10 && i < sortedList.size(); i++) {
                    top10Ids.add(sortedList.get(i).getLocationId());
                }

                System.out.printf("%-15s | %-15s | %-15d | %s\n",
                        path.substring(path.lastIndexOf("/") + 1),
                        report.getSorterName(),
                        report.getAverageNanos(),
                        String.join(", ", top10Ids));
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------");
        }

        return inspectionResults;
    }

    // ==================== Task B 实现 ====================

    private static void runTaskB(List<String> datasetA, List<String> datasetB, List<String> datasetC) {
        // 1. 加载图
        String pathFile = "CPT204/data/paths.csv";
        WeightedGraph graph = CSVLoader.loadWeightedGraph(pathFile);
        System.out.println("Graph loaded successfully!");
        System.out.println("Vertices: " + graph.getVertexCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        // 2. 打印 Top 10 信息
        System.out.println("\n=== Task A Top 10 Results ===");
        System.out.println("Dataset A: " + datasetA);
        System.out.println("Dataset B: " + datasetB);
        System.out.println("Dataset C: " + datasetC);

        // 3. 创建图引擎
        GraphEngine engine = new GraphEngine(graph);

        // 4. 计算四个 Case
        String a1 = datasetA.get(0);
        String a10 = datasetA.get(9);
        String b5 = datasetB.get(4);
        String b1 = datasetB.get(0);
        String c5 = datasetC.get(4);
        String c1 = datasetC.get(0);

        System.out.println("\n=== Case 1: A1 to itself ===");
        PathResult result1 = engine.shortestPath(a1, a1);
        System.out.println("Start: " + a1 + ", End: " + a1);
        System.out.println("Path: " + result1);

        System.out.println("\n=== Case 2: A1 to A10 ===");
        PathResult result2 = engine.shortestPath(a1, a10);
        if (result2.isReachable()) {
            System.out.println("Start: " + a1 + ", End: " + a10);
            System.out.println("Path: " + result2);
        } else {
            System.out.println("No path found from " + a1 + " to " + a10);
        }

        System.out.println("\n=== Case 3: A1 → B5 → B1 ===");
        PathResult result3 = engine.shortestPathWithWaypoints(a1, Arrays.asList(b5), b1);
        if (result3.isReachable()) {
            System.out.println("Start: " + a1 + ", via: " + b5 + ", End: " + b1);
            System.out.println("Path: " + result3);
        } else {
            System.out.println("No valid path found for Case 3");
        }

        System.out.println("\n=== Case 4: A1 → B5 → C5 → C1 ===");
        PathResult result4 = engine.shortestPathWithWaypoints(a1, Arrays.asList(b5, c5), c1);
        if (result4.isReachable()) {
            System.out.println("Start: " + a1 + ", via: " + b5 + " → " + c5 + ", End: " + c1);
            System.out.println("Path: " + result4);
        } else {
            System.out.println("No valid path found for Case 4");
        }

        // 1. 创建可视化所需的 Case 列表
        List<visualization.PathVisualizer.PathCase> pathCases = new ArrayList<>();

        pathCases.add(new visualization.PathVisualizer.PathCase("Case 1: A1 → A1",
                a1, a1, new ArrayList<>(), result1));

        pathCases.add(new visualization.PathVisualizer.PathCase("Case 2: A1 → A10",
                a1, a10, new ArrayList<>(), result2));

        pathCases.add(new visualization.PathVisualizer.PathCase("Case 3: A1 → B1 (via B5)",
                a1, b1, Arrays.asList(b5), result3));

        pathCases.add(new visualization.PathVisualizer.PathCase("Case 4: A1 → C1 (via B5, C5)",
                a1, c1, Arrays.asList(b5, c5), result4));

        // 2. 启动可视化
        javax.swing.SwingUtilities.invokeLater(() -> {
            visualization.PathVisualizer commandCenter = new visualization.PathVisualizer(pathCases);
            commandCenter.setVisible(true);
            System.out.println("\nPath visualization has been successfully initiated");
        });
    }
}