package main;

import model.Location;
import io.CSVLoader;
import algorithm.*;
import model.SortResult;
import java.util.*;

public class TestSort {

    private static final Map<String, List<String>> inspectionResults = new LinkedHashMap<>();

    public static void main(String[] args) {
        // 1. 定义数据集路径
        String[] filePaths = {
                "CPT204/data/candidates_A.csv",
                "CPT204/data/candidates_B.csv",
                "CPT204/data/candidates_C.csv"
        };

        // 2. 初始化排序算法（多态）
        List<Sorter> sorters = Arrays.asList(
                new BubbleSorter(),
                new QuickSorter(),
                new MergeSorter()
        );

        // 3. 创建评测器：【关键修改】
        // 参数 1000 代表正式运行 1000 次取平均值
        // 参数 20 代表在计时前先静默预热运行 20 次，消除 JVM 冷启动噪音
        SortPerfomance benchmark = new SortPerfomance(sorters, 1000, 20);

        // 打印表头
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s | %s\n",
                "Dataset", "Algorithm", "Avg Time (ns)", "Top 10 Selected Locations");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String path : filePaths) {
            String datasetName = path.substring(path.lastIndexOf("_") + 1, path.lastIndexOf("."));

            // 加载数据
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData == null || originalData.isEmpty()) continue;

            // 4. 使用修改后的 benchmark 类运行
            List<SortResult> reports = benchmark.run(originalData);

            if (!reports.isEmpty()) {
                List<Location> fullSortedList = reports.get(0).getSortedData();
                List<String> top10Ids = new ArrayList<>();
                for (int i = 0; i < 10 && i < fullSortedList.size(); i++) {
                    top10Ids.add(fullSortedList.get(i).getLocationId());
                }
                inspectionResults.put(datasetName, top10Ids);
            }

            // 5. 打印输出
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
    }

    public static Map<String, List<String>> getInspectionResults() {
        return inspectionResults;
    }
}