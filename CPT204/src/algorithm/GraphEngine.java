package algorithm;

import model.PathResult;
import model.WeightedEdge;
import model.WeightedGraph;

import java.util.*;

public class GraphEngine {
    private WeightedGraph graph;

    public GraphEngine(WeightedGraph graph) {
        this.graph = graph;
    }

    public PathResult shortestPath(String start, String end) {
        // Check if start and end vertices exist in the graph
        if (!graph.containsVertex(start)) {
            System.err.println("Start vertex not in graph: " + start);
            return PathResult.unreachable();
        }
        if (!graph.containsVertex(end)) {
            System.err.println("End vertex not in graph: " + end);
            return PathResult.unreachable();
        }
        // Case: start equals end
        if (start.equals(end)) {
            List<String> path = new ArrayList<>();
            path.add(start);
            return new PathResult(path, 0.0);
        }
        // Use maps instead of arrays because vertex IDs are strings (e.g., "L0001")
        // and cannot be used directly as array indices

        // Distance map: vertex -> shortest distance from start (cost[])
        Map<String, Double> distance = new HashMap<>();
        // Predecessor map: vertex -> previous vertex on the shortest path (parent[])
        Map<String, String> previous = new HashMap<>();
        // Min-heap priority queue: stores (distance, vertex) sorted by distance
        PriorityQueue<Map.Entry<Double, String>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getKey)
        );
        // Set of visited vertices
        Set<String> visited = new HashSet<>();

        // Initialize: distance = infinity, predecessor = null
        for (String vertex : graph.getAllVertices()) {
            distance.put(vertex, Double.POSITIVE_INFINITY);
            previous.put(vertex, null);
        }
        // Initialize start vertex with distance 0
        distance.put(start, 0.0);
        // Insert (distance=0.0, vertex=start) into the priority queue
        pq.offer(new AbstractMap.SimpleEntry<>(0.0, start));
        //AbstractMap.SimpleEntry is a simple key value pair implementation class provided by Java.
        //We only need a simple key value pair and do not require all the features of HashMap, so we do not use HashMap

        while (!pq.isEmpty()) {
            // Extract the vertex with the smallest distance
            Map.Entry<Double, String> entry = pq.poll();
            String current = entry.getValue();
            double currentDist = entry.getKey();

            // Lazy deletion: skip if already visited
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Early termination: reached the destination
            if (current.equals(end)) {
                break;
            }

            // Relax all neighbors of the current vertex
            for (WeightedEdge edge : graph.getNeighbors(current)) {
                String neighbor = edge.getTo();
                double weight = edge.getWeight();

                if (visited.contains(neighbor)) {
                    continue;
                }

                double newDist = currentDist + weight;
                if (newDist < distance.get(neighbor)) {
                    // Found a shorter path: update distance and predecessor
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    // Insert new entry into PQ (old entries remain, will be skipped via lazy deletion)
                    pq.offer(new AbstractMap.SimpleEntry<>(newDist, neighbor));
                }
            }
        }

        // Check if a path was found
        if (distance.get(end) == Double.POSITIVE_INFINITY) {
            System.err.println("No path found from " + start + " to " + end);
            return PathResult.unreachable();
        }
        // Reconstruct the path
        List<String> path = reconstructPath(start, end, previous);

        // If path reconstruction failed, return unreachable
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return PathResult.unreachable();
        }

        double totalDistance = distance.get(end);

        return new PathResult(path, totalDistance);
    }

    private List<String> reconstructPath(String start, String end, Map<String, String> previous) {
        List<String> path = new ArrayList<>();
        String current = end;

        // If end has no predecessor and start != end, the destination is unreachable
        if (previous.get(end) == null && !start.equals(end)) {
            return path;
        }

        // Backtrack from end to start
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        // Reverse to get correct order from start to end
        Collections.reverse(path);

        // Verify that the path starts with the correct start vertex
        if (!path.isEmpty() && !path.get(0).equals(start)) {
            return new ArrayList<>();
        }

        return path;
    }

    public PathResult shortestPathWithWaypoints(String start, List<String> waypoints, String end) {
        List<String> fullPath = new ArrayList<>();
        double totalDistance = 0.0;
        String currentStart = start;

        // Process each segment: start -> waypoint1 -> waypoint2 -> ... -> last waypoint
        for (String waypoint : waypoints) {
            PathResult segment = shortestPath(currentStart, waypoint);
            if (!segment.isReachable()) {
                System.err.println("Cannot reach waypoint " + waypoint + " from " + currentStart);
                return PathResult.unreachable();
            }
            // Concatenate paths (skip duplicate start point)
            if (fullPath.isEmpty()) {
                fullPath.addAll(segment.getPath());
            } else {
                List<String> segmentPath = segment.getPath();
                if (segmentPath.size() <= 1) {
                    return PathResult.unreachable();
                }
                fullPath.addAll(segmentPath.subList(1, segmentPath.size()));
            }
            totalDistance += segment.getTotalDistance();
            currentStart = waypoint;
        }

        // Final segment: last waypoint -> end
        PathResult lastSegment = shortestPath(currentStart, end);
        if (!lastSegment.isReachable()) {
            System.err.println("Cannot reach end " + end + " from " + currentStart);
            return PathResult.unreachable();
        }

        // Concatenate the final segment
        List<String> lastPath = lastSegment.getPath();
        if (lastPath.size() <= 1) {
            return PathResult.unreachable();
        }
        fullPath.addAll(lastPath.subList(1, lastPath.size()));
        totalDistance += lastSegment.getTotalDistance();

        return new PathResult(fullPath, totalDistance);
    }
}