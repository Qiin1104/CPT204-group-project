package io;
import model.Location;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import model.WeightedEdge;
import model.WeightedGraph;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVLoader {

    public static List<Location> loadLocations(String filePath) {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 跳过空行或非数据行
                if (line.trim().isEmpty() || line.contains("location_id")) {
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String id = values[0].trim();
                    double score = Double.parseDouble(values[1].trim());
                    locations.add(new Location(id,score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("读取文件失败: " + filePath + "，错误原因: " + e.getMessage());
        }
        return locations;
    }

    /**
     * 从 paths.csv 加载加权图
     * @param filePath CSV文件路径
     * @return 构建好的 WeightedGraph 对象
     */
    public static WeightedGraph loadWeightedGraph(String filePath) {
        WeightedGraph graph = new WeightedGraph();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // 跳过空行
                if (line.trim().isEmpty()) continue;

                // 跳过标题行（假设第一行是 from_location,to_location,weight）
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    double weight;

                    try {
                        weight = Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid weight value: " + parts[2]);
                        continue;
                    }

                    graph.addEdge(from, to, weight);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return graph;
    }

    /**
     * 加载三个候选数据集的前10位置ID（从 Task A 结果）
     * @return Map<数据集名称, List<位置ID>>
     */
    public static Map<String, List<String>> loadTop10Locations() {
        Map<String, List<String>> result = new HashMap<>();

        // 根据 Task A 的结果填入
        List<String> datasetA = Arrays.asList(
                "L0001", "L0002", "L0003", "L0004", "L0005",
                "L0006", "L0007", "L0008", "L0009", "L0010"
        );

        List<String> datasetB = Arrays.asList(
                "L0101", "L0102", "L0103", "L0104", "L0105",
                "L0106", "L0107", "L0108", "L0109", "L0110"
        );

        List<String> datasetC = Arrays.asList(
                "L0201", "L0202", "L0203", "L0204", "L0205",
                "L0206", "L0207", "L0208", "L0209", "L0210"
        );

        result.put("A", datasetA);
        result.put("B", datasetB);
        result.put("C", datasetC);

        return result;
    }
}