package io;

import model.Location;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    locations.add(new Location(id, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("读取文件失败: " + filePath + "，错误原因: " + e.getMessage());
        }
        return locations;
    }
}