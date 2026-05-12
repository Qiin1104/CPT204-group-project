package main;

import model.Location;
import io.CSVLoader;
import algorithm.*;
import algorithm.SortTester;    // 导入你新建的评测类
import model.SortResult; // 导入结果封装类
import java.util.*;

public class TestSort {
    public static void main(String[] args) {
        // 1. 定义数据集路径 [cite: 32, 33, 34, 35]
        String[] filePaths = {
                "CPT204/data/candidates_A.csv",
                "CPT204/data/candidates_B.csv",
                "CPT204/data/candidates_C.csv"
        };

        // 2. 初始化排序算法（多态） [cite: 48, 49, 50]
        List<Sorter> sorters = Arrays.asList(
                new BubbleSorter(),
                new QuickSorter(),
                new MergeSorter()
        );

        // 3. 创建评测器：重复 3 次以减少误差
        SortTester benchmark = new SortTester(sorters, 3);

        // 打印任务书要求的表头格式 [cite: 69]
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s | %s\n",
                "Dataset", "Algorithm", "Avg Time (ns)", "Top 10 Selected Locations");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String path : filePaths) {
            // 加载数据
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData == null || originalData.isEmpty()) continue;

            // 4. 使用 benchmark 类运行所有算法并获取结果
            List<SortResult> reports = benchmark.run(originalData);

            for (SortResult report : reports) {
                // 5. 提取 Top 10 ID
                List<Location> sortedList = report.getSortedData();
                List<String> top10Ids = new ArrayList<>();
                for (int i = 0; i < 10 && i < sortedList.size(); i++) {
                    top10Ids.add(sortedList.get(i).getLocationId());
                }

                // 格式化输出
                System.out.printf("%-15s | %-15s | %-15d | %s\n",
                        path.substring(path.lastIndexOf("/") + 1),
                        report.getSorterName(),
                        report.getAverageNanos(),
                        String.join(", ", top10Ids));
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------");
        }
    }
}