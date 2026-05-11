package model;


    public class Location {
        private String locationId;
        private double priorityScore;

        public Location(String locationId, double priorityScore) {
            this.locationId = locationId;
            this.priorityScore = priorityScore;
        }

        public String getLocationId() {
            return locationId;
        }

        public double getPriorityScore() {
            return priorityScore;
        }

        // 为了方便打印结果，重写 toString
        @Override
        public String toString() {
            return String.format("%s (Score: %.2f)", locationId, priorityScore);
        }
    }