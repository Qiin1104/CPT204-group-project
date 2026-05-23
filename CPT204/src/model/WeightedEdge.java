package model;


public class WeightedEdge {
    private String from;
    private String to;
    private double weight;

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