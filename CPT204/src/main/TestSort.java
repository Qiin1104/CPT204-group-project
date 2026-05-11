package main;

import model.Location;
import io.CSVLoader;
import algorithm.*;
import java.util.*;

public class TestSort {
    public static void main(String[] args) {
        // 1. 定义数据集路径
        String[] filePaths = {
                "CPT204/data/candidates_A.csv",
                "CPT204/data/candidates_B.csv",
                "CPT204/data/candidates_C.csv"
        };

        LocationComparator comparator = new LocationComparator();

        // 打印表头
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        // 将 %-s 改为 %s
        System.out.printf("%-20s | %-12s | %-12s | %-12s | %s\n",
                "Dataset", "Bubble (ns)", "Quick (ns)", "Merge (ns)", "Top 10 Selected Locations");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");

        for (String path : filePaths) {
            // 读取原始数据
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData.isEmpty()) continue;

            // --- 测量 Bubble Sort (跑3次取平均) ---
            long bubbleTotal = 0;
            for (int i = 0; i < 3; i++) {
                List<Location> copy = new ArrayList<>(originalData);
                long start = System.nanoTime();
                SortingEngine.bubbleSort(copy, comparator);
                bubbleTotal += (System.nanoTime() - start);
            }
            long bubbleAvg = bubbleTotal / 3;

            // --- 测量 Quick Sort (跑3次取平均) ---
            long quickTotal = 0;
            for (int i = 0; i < 3; i++) {
                List<Location> copy = new ArrayList<>(originalData);
                long start = System.nanoTime();
                SortingEngine.quickSort(copy, 0, copy.size() - 1, comparator);
                quickTotal += (System.nanoTime() - start);
            }
            long quickAvg = quickTotal / 3;

            // --- 测量 Merge Sort (跑3次取平均) ---
            long mergeTotal = 0;
            for (int i = 0; i < 3; i++) {
                List<Location> copy = new ArrayList<>(originalData);
                long start = System.nanoTime();
                SortingEngine.mergeSort(copy, comparator);
                mergeTotal += (System.nanoTime() - start);
            }
            long mergeAvg = mergeTotal / 3;

            // --- 获取该数据集的 Top 10 ---
            // 使用 MergeSort 得到一个稳定的最终排序结果（或者用 QuickSort 也可以）
            List<Location> sortedResult = new ArrayList<>(originalData);
            SortingEngine.mergeSort(sortedResult, comparator);

            List<String> top10Ids = new ArrayList<>();
            for (int i = 0; i < 10 && i < sortedResult.size(); i++) {
                top10Ids.add(sortedResult.get(i).getLocationId());
            }

            // 将 Top 10 列表转换为逗号分隔的字符串
            String top10String = String.join(", ", top10Ids);


            System.out.printf("%-20s | %-12d | %-12d | %-12d | %s\n",
                    path, bubbleAvg, quickAvg, mergeAvg, top10String);
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
    }
}