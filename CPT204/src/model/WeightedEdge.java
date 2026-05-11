package model;


/**
 * 加权边类 - 表示图中的一条边
 * 用于带权重的图（如路径距离）
 */
public class WeightedEdge {
    private String from;      // 起点
    private String to;        // 终点
    private double weight;    // 权重（距离）

    public WeightedEdge(String from, String to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getWeight() { return weight; }

    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return from + " → " + to + " (" + weight + ")";
    }
}