package model;

import java.util.*;

public class WeightedGraph {
    private Map<String, List<WeightedEdge>> adjacencyList;
    private int edgeCount;      // 边数


    public WeightedGraph() {
        this.adjacencyList = new HashMap<>();
        this.edgeCount = 0;
    }

    public void addEdge(String from, String to, double weight) {
        if (!adjacencyList.containsKey(from)) {
            adjacencyList.put(from, new ArrayList<>());
        }

        adjacencyList.get(from).add(new WeightedEdge(from, to, weight));


        if (!adjacencyList.containsKey(to)) {
            adjacencyList.put(to, new ArrayList<>());
        }
        adjacencyList.get(to).add(new WeightedEdge(to, from, weight));

        edgeCount++;
    }


    public List<WeightedEdge> getNeighbors(String vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }


    public Set<String> getAllVertices() {
        return adjacencyList.keySet();
    }

    public boolean containsVertex(String vertex) {
        return adjacencyList.containsKey(vertex);
    }

    public int getVertexCount() {
        return adjacencyList.size();
    }

    public int getEdgeCount() {
        return edgeCount;
    }

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