package main;

import model.Location;
import io.CSVLoader;
import algorithm.*;
import model.SortResult;
import java.util.*;

public class TestSort {

    // 【新增】提供给 Task B 使用的数据结构
    // Key: 数据集名称 (如 "A"), Value: 该数据集排名前 10 的地点对象列表
    private static final Map<String, List<Location>> inspectionResults = new LinkedHashMap<>();

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

        // 3. 创建评测器：重复 3 次以减少误差
        SortTester benchmark = new SortTester(sorters, 3);

        // 打印表头
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s | %s\n",
                "Dataset", "Algorithm", "Avg Time (ns)", "Top 10 Selected Locations");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String path : filePaths) {
            // 解析数据集名称 (例如从 candidates_A.csv 中提取 "A")
            String datasetName = path.substring(path.lastIndexOf("_") + 1, path.lastIndexOf("."));

            // 加载数据
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData == null || originalData.isEmpty()) continue;

            // 4. 使用 benchmark 类运行所有算法并获取结果报告
            List<SortResult> reports = benchmark.run(originalData);

            // 【关键修改】存储该数据集的最终结果供 Task B 使用
            // 我们只需要取任意一个算法的排序结果（因为规则一致，结果相同）
            if (!reports.isEmpty()) {
                List<Location> fullSortedList = reports.get(0).getSortedData();
                List<Location> top10 = new ArrayList<>(
                        fullSortedList.subList(0, Math.min(10, fullSortedList.size()))
                );
                inspectionResults.put(datasetName, top10);
            }

            // 5. 打印输出（用于实验报告截图）
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


    /**
     * 【新增】提供给同伴的静态访问方法
     * @return 包含三个数据集 Top 10 地点的 Map
     */
    public static Map<String, List<Location>> getInspectionResults() {
        return inspectionResults;
    }

}