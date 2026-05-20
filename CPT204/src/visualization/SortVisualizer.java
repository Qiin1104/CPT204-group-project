package visualization;

import algorithm.BubbleSorter;
import algorithm.MergeSorter;
import algorithm.QuickSorter;
import algorithm.SortPerfomance;
import algorithm.Sorter;
import io.CSVLoader;
import model.Location;
import model.SortResult;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SortVisualizer extends JFrame {
    private static final int BENCHMARK_REPETITIONS = 1000;
    private static final int WARMUP_REPETITIONS = 20;
    private static final String OUTPUT_PATH = "CPT204/output/sort_runtime_chart.png";

    public static final class DatasetResult {
        private final String datasetName;
        private final List<SortResult> results;

        public DatasetResult(String datasetName, List<SortResult> results) {
            this.datasetName = datasetName;
            this.results = new ArrayList<>(results);
        }
    }

    private final SortChartPanel chartPanel;

    public static void main(String[] args) {
        List<DatasetResult> visualizationData = buildVisualizationData();

        try {
            saveChartImage(visualizationData, OUTPUT_PATH, BENCHMARK_REPETITIONS, WARMUP_REPETITIONS);
            System.out.println("Sorting runtime chart saved to " + OUTPUT_PATH);
        } catch (IOException e) {
            System.err.println("Failed to save sorting runtime chart: " + e.getMessage());
        }

        if (!GraphicsEnvironment.isHeadless()) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                SortVisualizer visualizer = new SortVisualizer(
                        visualizationData, BENCHMARK_REPETITIONS, WARMUP_REPETITIONS);
                visualizer.setVisible(true);
            });
        }
    }

    private static List<DatasetResult> buildVisualizationData() {
        String[] filePaths = {
                "CPT204/data/candidates_A.csv",
                "CPT204/data/candidates_B.csv",
                "CPT204/data/candidates_C.csv"
        };

        List<Sorter> sorters = Arrays.asList(
                new BubbleSorter(),
                new QuickSorter(),
                new MergeSorter()
        );

        SortPerfomance benchmark = new SortPerfomance(sorters, BENCHMARK_REPETITIONS, WARMUP_REPETITIONS);
        List<DatasetResult> visualizationData = new ArrayList<>();

        for (String path : filePaths) {
            String datasetName = path.substring(path.lastIndexOf("_") + 1, path.lastIndexOf("."));
            List<Location> originalData = CSVLoader.loadLocations(path);
            if (originalData == null || originalData.isEmpty()) {
                continue;
            }
            List<SortResult> reports = benchmark.run(originalData);
            visualizationData.add(new DatasetResult("Dataset " + datasetName, reports));
        }

        return visualizationData;
    }

    public SortVisualizer(List<DatasetResult> datasetResults) {
        this(datasetResults, 0, 0);
    }

    public SortVisualizer(List<DatasetResult> datasetResults, int repetitions, int warmupRepetitions) {
        this.chartPanel = new SortChartPanel(datasetResults, repetitions, warmupRepetitions);
        setTitle("Sorting Algorithm Runtime Analysis");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    public static void saveChartImage(List<DatasetResult> datasetResults, String filePath) throws IOException {
        saveChartImage(datasetResults, filePath, 0, 0);
    }

    public static void saveChartImage(List<DatasetResult> datasetResults, String filePath,
                                      int repetitions, int warmupRepetitions) throws IOException {
        SortChartPanel panel = new SortChartPanel(datasetResults, repetitions, warmupRepetitions);
        panel.setSize(1000, 700);

        File output = new File(filePath);
        File parent = output.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                panel.getWidth(), panel.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        g2.dispose();
        ImageIO.write(image, "png", output);
    }

    private static final class SortChartPanel extends JPanel {
        private static final Color BG = new Color(248, 250, 252);
        private static final Color AXIS = new Color(51, 65, 85);
        private static final Color GRID = new Color(203, 213, 225);
        private static final Color TEXT = new Color(15, 23, 42);
        private static final Color BUBBLE = new Color(148, 163, 184);
        private static final Color QUICK = new Color(244, 184, 170);
        private static final Color MERGE = new Color(185, 160, 190);

        private final List<DatasetResult> datasetResults;
        private final int repetitions;
        private final int warmupRepetitions;

        private SortChartPanel(List<DatasetResult> datasetResults, int repetitions, int warmupRepetitions) {
            this.datasetResults = new ArrayList<>(datasetResults);
            this.repetitions = repetitions;
            this.warmupRepetitions = warmupRepetitions;
            setPreferredSize(new Dimension(1000, 700));
            setBackground(BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 105;
            int right = 165;
            int top = 95;
            int bottom = 110;
            int plotW = width - left - right;
            int plotH = height - top - bottom;

            g2.setColor(Color.WHITE);
            g2.fillRect(left, top, plotW, plotH);
            g2.setColor(new Color(226, 232, 240));
            g2.drawRect(left, top, plotW, plotH);

            double minMs = Math.max(0.001, findMinMillis());
            double maxMs = Math.max(minMs * 10.0, findMaxMillis());
            double minLog = Math.floor(Math.log10(minMs));
            double maxLog = Math.ceil(Math.log10(maxMs));

            drawTitle(g2, width);
            drawAxesAndGrid(g2, left, top, plotW, plotH, minLog, maxLog);
            drawBars(g2, left, top, plotW, plotH, minLog, maxLog);
            drawLegend(g2, width - right + 35, top + 20);

            g2.dispose();
        }

        private void drawTitle(Graphics2D g2, int width) {
            String title = "Sorting Algorithm Runtime Analysis";
            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (width - fm.stringWidth(title)) / 2, 45);

            String subtitle = "Average runtime comparison for candidate datasets";
            if (repetitions > 0) {
                subtitle += " | Benchmark runs: " + repetitions + " | Warm-up runs: " + warmupRepetitions;
            }
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fm = g2.getFontMetrics();
            g2.setColor(new Color(71, 85, 105));
            g2.drawString(subtitle, (width - fm.stringWidth(subtitle)) / 2, 68);
        }

        private void drawAxesAndGrid(Graphics2D g2, int left, int top, int plotW, int plotH,
                                     double minLog, double maxLog) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setStroke(new BasicStroke(1f));

            int tickStart = (int) minLog;
            int tickEnd = (int) maxLog;
            for (int tick = tickStart; tick <= tickEnd; tick++) {
                double yRatio = (maxLog - tick) / (maxLog - minLog);
                int y = top + (int) Math.round(yRatio * plotH);
                g2.setColor(GRID);
                g2.drawLine(left, y, left + plotW, y);
                g2.setColor(AXIS);
                String label = formatPowerOfTen(tick);
                g2.drawString(label, left - 58, y + 4);
            }

            g2.setColor(AXIS);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawLine(left, top, left, top + plotH);
            g2.drawLine(left, top + plotH, left + plotW, top + plotH);

            String yLabel = "Time (ms, log scale)";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.rotate(-Math.PI / 2);
            g2.drawString(yLabel, -(top + plotH / 2 + 70), 35);
            g2.rotate(Math.PI / 2);

            String xLabel = "Dataset";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(xLabel, left + (plotW - fm.stringWidth(xLabel)) / 2, top + plotH + 75);
        }

        private void drawBars(Graphics2D g2, int left, int top, int plotW, int plotH,
                              double minLog, double maxLog) {
            if (datasetResults.isEmpty()) {
                return;
            }

            int groups = datasetResults.size();
            int groupW = plotW / groups;
            int barW = Math.max(26, groupW / 7);
            int gap = Math.max(8, barW / 3);
            Color[] colors = {BUBBLE, QUICK, MERGE};

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (int i = 0; i < groups; i++) {
                DatasetResult dataset = datasetResults.get(i);
                int groupCenter = left + groupW * i + groupW / 2;
                int totalBarW = dataset.results.size() * barW + (dataset.results.size() - 1) * gap;
                int startX = groupCenter - totalBarW / 2;

                for (int j = 0; j < dataset.results.size(); j++) {
                    SortResult result = dataset.results.get(j);
                    double ms = Math.max(0.001, result.getAverageNanos() / 1_000_000.0);
                    double log = Math.log10(ms);
                    double ratio = (log - minLog) / (maxLog - minLog);
                    int barH = Math.max(3, (int) Math.round(ratio * plotH));
                    int x = startX + j * (barW + gap);
                    int y = top + plotH - barH;

                    g2.setColor(colors[j % colors.length]);
                    g2.fillRect(x, y, barW, barH);
                    g2.setColor(new Color(100, 116, 139));
                    g2.drawRect(x, y, barW, barH);

                    String value = String.format("%.3f", ms);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(TEXT);
                    g2.drawString(value, x + (barW - fm.stringWidth(value)) / 2, y - 6);
                }

                g2.setColor(TEXT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(dataset.datasetName, groupCenter - fm.stringWidth(dataset.datasetName) / 2,
                        top + plotH + 28);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            String[] names = {"Bubble Sort", "Quick Sort", "Merge Sort"};
            Color[] colors = {BUBBLE, QUICK, MERGE};
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            for (int i = 0; i < names.length; i++) {
                int itemY = y + i * 28;
                g2.setColor(colors[i]);
                g2.fillRect(x, itemY - 12, 18, 18);
                g2.setColor(new Color(100, 116, 139));
                g2.drawRect(x, itemY - 12, 18, 18);
                g2.setColor(TEXT);
                g2.drawString(names[i], x + 28, itemY + 2);
            }
        }

        private double findMinMillis() {
            double min = Double.POSITIVE_INFINITY;
            for (DatasetResult dataset : datasetResults) {
                for (SortResult result : dataset.results) {
                    min = Math.min(min, result.getAverageNanos() / 1_000_000.0);
                }
            }
            return min == Double.POSITIVE_INFINITY ? 0.001 : min;
        }

        private double findMaxMillis() {
            double max = 0.001;
            for (DatasetResult dataset : datasetResults) {
                for (SortResult result : dataset.results) {
                    max = Math.max(max, result.getAverageNanos() / 1_000_000.0);
                }
            }
            return max;
        }

        private String formatPowerOfTen(int exponent) {
            if (exponent == 0) {
                return "1";
            }
            return "10^" + exponent;
        }
    }
}
