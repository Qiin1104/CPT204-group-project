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
                //Skip empty lines or non-data lines.
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
            System.err.println("Failed to read file: " + filePath + ", Error reason: " + e.getMessage());
        }
        return locations;
    }

    /**
     * Load the weighted graph from paths.csv.
     * @param filePath The path to the CSV file.
     * @return The constructed WeightedGraph object.
     */
    public static WeightedGraph loadWeightedGraph(String filePath) {
        WeightedGraph graph = new WeightedGraph();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                // Skip the header row (assuming the first row is from_location,to_location,weight)
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
     * Load the top 10 location IDs from the three candidate datasets (from Task A results).
     * @return Map<dataset name, List<location ID>>
     */
    public static Map<String, List<String>> loadTop10Locations() {
        Map<String, List<String>> result = new HashMap<>();

        // Fill in based on Task A results.
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