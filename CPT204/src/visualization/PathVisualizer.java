package visualization;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import model.PathResult;

public class PathVisualizer extends JFrame {

    private static final Color COLOR_BG = new Color(11, 15, 26);
    private static final Color COLOR_SIDEBAR = new Color(18, 24, 41);
    private static final Color COLOR_CARD = new Color(26, 35, 58);
    private static final Color COLOR_GRID = new Color(33, 44, 71, 80);
    private static final Color COLOR_TEXT_MAIN = new Color(240, 246, 252);
    private static final Color COLOR_TEXT_MUTED = new Color(139, 148, 158);
    private static final Color NEON_START = new Color(0, 242, 165);
    private static final Color NEON_END = new Color(255, 46, 99);
    private static final Color NEON_NORMAL = new Color(56, 139, 253);
    private static final Color NODE_START_COLOR = new Color(255, 255, 255);
    private static final Color NODE_WAYPOINT_COLOR = new Color(241, 196, 15);
    private static final Color NODE_END_COLOR = new Color(255, 75, 75);
    private static final Color NODE_DEFAULT_COLOR = new Color(56, 139, 253);

    private List<PathCase> pathCases;
    private int currentCaseIndex = 0;

    private MapPanel mapPanel;
    private JPanel detailPanel;
    private JLabel lblDistance;
    private JLabel lblNodeCount;
    private JTextArea txtPathFlow;

    public static class PathCase {
        public String caseName;
        public String start;
        public String destination;
        public List<String> waypoints;
        public PathResult result;

        public PathCase(String caseName, String start, String destination, List<String> waypoints, PathResult result) {
            this.caseName = caseName;
            this.start = start;
            this.destination = destination;
            this.waypoints = new ArrayList<>(waypoints);
            this.result = result;
        }
    }

    public PathVisualizer(List<PathCase> pathCases) {
        this.pathCases = pathCases;
        initUI();
    }

    private void initUI() {
        setTitle("Path Result Visualization");
        setSize(1350, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(340, 800));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_GRID));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Path Result");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT_MAIN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 3, 20));
        sidebar.add(lblTitle);

        JPanel guidePanel = new JPanel(new BorderLayout());
        guidePanel.setBackground(new Color(56, 139, 253, 25));
        guidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(56, 139, 253, 80)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        JLabel lblGuide = new JLabel("Click the button below to switch path views");
        lblGuide.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblGuide.setForeground(new Color(0, 242, 165));
        guidePanel.add(lblGuide, BorderLayout.CENTER);
        sidebar.add(guidePanel);

        JPanel btnGroup = new JPanel(new GridLayout(4, 1, 0, 10));
        btnGroup.setBackground(COLOR_SIDEBAR);
        btnGroup.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (int i = 0; i < pathCases.size(); i++) {
            final int index = i;
            JButton btn = new JButton(pathCases.get(i).caseName);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setForeground(COLOR_TEXT_MAIN);
            btn.setBackground(COLOR_CARD);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(56, 139, 253, 120), 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(41, 98, 255));
                    btn.setForeground(Color.WHITE);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(COLOR_CARD);
                    btn.setForeground(COLOR_TEXT_MAIN);
                }
            });

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentCaseIndex = index;
                    updateDashboard();
                }
            });
            btnGroup.add(btn);
        }
        sidebar.add(btnGroup);

        detailPanel = new JPanel();
        detailPanel.setBackground(COLOR_CARD);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 10, 15),
                BorderFactory.createLineBorder(COLOR_GRID, 1, true)
        ));
        detailPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblDistTag = new JLabel("Total Distance of the Path");
        lblDistTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDistTag.setForeground(COLOR_TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 0; detailPanel.add(lblDistTag, gbc);

        lblDistance = new JLabel("0.0 m");
        lblDistance.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblDistance.setForeground(NEON_START);
        gbc.gridy = 1; detailPanel.add(lblDistance, gbc);

        JLabel lblNodeTag = new JLabel("Target Nodes Traversed");
        lblNodeTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNodeTag.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 2; detailPanel.add(lblNodeTag, gbc);

        lblNodeCount = new JLabel("0 Nodes");
        lblNodeCount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNodeCount.setForeground(COLOR_TEXT_MAIN);
        gbc.gridy = 3; detailPanel.add(lblNodeCount, gbc);

        JLabel lblFlowTag = new JLabel("Logical Path Sequence");
        lblFlowTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFlowTag.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 4; detailPanel.add(lblFlowTag, gbc);

        txtPathFlow = new JTextArea(4, 15);
        txtPathFlow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPathFlow.setForeground(COLOR_TEXT_MAIN);
        txtPathFlow.setBackground(COLOR_SIDEBAR);
        txtPathFlow.setLineWrap(true);
        txtPathFlow.setWrapStyleWord(true);
        txtPathFlow.setEditable(false);
        txtPathFlow.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane textScroll = new JScrollPane(txtPathFlow);
        textScroll.setBorder(BorderFactory.createLineBorder(COLOR_GRID));
        textScroll.getViewport().setBackground(COLOR_SIDEBAR);
        gbc.gridy = 5; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        detailPanel.add(textScroll, gbc);

        sidebar.add(detailPanel);

        JPanel legendPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        legendPanel.setOpaque(false);
        legendPanel.setBackground(COLOR_SIDEBAR);
        legendPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_GRID, 1),
                " Node Color Description ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11), COLOR_TEXT_MUTED
        ));

        legendPanel.add(createLegendItem("Start Node", NODE_START_COLOR, "The starting node of the path"));
        legendPanel.add(createLegendItem("Mandatory Waypoint", NODE_WAYPOINT_COLOR, "Mandatory nodes to pass through"));
        legendPanel.add(createLegendItem("Destination", NODE_END_COLOR, "The target point of the current path"));
        legendPanel.add(createLegendItem("Ordinary Waypoint", NODE_DEFAULT_COLOR, "Ordinary passing nodes in the path"));

        JPanel legendWrapper = new JPanel(new BorderLayout());
        legendWrapper.setOpaque(false);
        legendWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        legendWrapper.add(legendPanel, BorderLayout.CENTER);

        sidebar.add(legendWrapper);
        add(sidebar, BorderLayout.WEST);

        mapPanel = new MapPanel();
        add(mapPanel, BorderLayout.CENTER);

        updateDashboard();
    }

    private static JPanel createLegendItem(String title, Color dotColor, String description) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);

        JLabel lblDot = new JLabel(new javax.swing.Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(x + 2, y + 2, 10, 10);
                g2.setColor(new Color(dotColor.getRed(), dotColor.getGreen(), dotColor.getBlue(), 60));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, 14, 14);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });

        JPanel textWrapper = new JPanel(new GridLayout(2, 1, 0, 1));
        textWrapper.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDesc.setForeground(COLOR_TEXT_MUTED);

        textWrapper.add(lblTitle);
        textWrapper.add(lblDesc);

        item.add(lblDot, BorderLayout.WEST);
        item.add(textWrapper, BorderLayout.CENTER);
        return item;
    }

    private void updateDashboard() {
        PathCase rc = pathCases.get(currentCaseIndex);
        if (rc.result.isReachable()) {
            lblDistance.setText(String.format("%.1f m", rc.result.getTotalDistance()));
            lblNodeCount.setText(rc.result.getPath().size() + " Fixed Nodes");
            lblDistance.setForeground(NEON_START);
            String pathStr = String.join(" -> ", rc.result.getPath());
            txtPathFlow.setText(pathStr);
        } else {
            lblDistance.setText("DISCONNECTED");
            lblDistance.setForeground(NEON_END);
            lblNodeCount.setText("0 Nodes");
            txtPathFlow.setText("Warning: Selected structural points are mutually unreachable in the current topology network.");
        }
        mapPanel.repaint();
    }

    private class MapPanel extends JPanel {
        public MapPanel() {
            setBackground(COLOR_BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(COLOR_GRID);
            g2.setStroke(new BasicStroke(1.0f));
            int gridSize = 50;
            for (int x = 0; x < w; x += gridSize) {
                g2.drawLine(x, 0, x, h);
            }
            for (int y = 0; y < h; y += gridSize) {
                g2.drawLine(0, y, w, y);
            }

            PathCase rc = pathCases.get(currentCaseIndex);
            if (!rc.result.isReachable()) return;

            List<String> pathNodes = rc.result.getPath();

            Set<UnorderedPair<String>> bidiSegments = findBidirectionalSegments(pathNodes);

            Map<String, Point2D> nodePositions = new HashMap<>();
            for (int i = 0; i < pathNodes.size(); i++) {
                String node = pathNodes.get(i);
                nodePositions.put(node, calculateCoordinates(node, i, pathNodes.size(), w, h));
            }

            if (pathNodes.size() > 1) {
                g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < pathNodes.size() - 1; i++) {
                    Point2D p1 = nodePositions.get(pathNodes.get(i));
                    Point2D p2 = nodePositions.get(pathNodes.get(i + 1));

                    g2.setColor(new Color(56, 139, 253, 40));
                    g2.setStroke(new BasicStroke(9.0f));
                    g2.draw(new Line2D.Double(p1, p2));

                    g2.setPaint(new GradientPaint(
                            (float)p1.getX(), (float)p1.getY(), NEON_NORMAL,
                            (float)p2.getX(), (float)p2.getY(), NEON_START));
                    g2.setStroke(new BasicStroke(4.0f));
                    g2.draw(new Line2D.Double(p1, p2));
                }
            }

            if (pathNodes.size() > 1) {
                for (int i = 0; i < pathNodes.size() - 1; i++) {
                    Point2D p1 = nodePositions.get(pathNodes.get(i));
                    Point2D p2 = nodePositions.get(pathNodes.get(i + 1));

                    double tipPosFraction = 0.5;

                    if (bidiSegments.contains(new UnorderedPair<>(pathNodes.get(i), pathNodes.get(i + 1)))) {
                        tipPosFraction = 0.75;
                    }

                    drawDynamicArrow(g2, p1, p2, tipPosFraction);
                }
            }

            int nodeRadius = 24;
            for (int i = 0; i < pathNodes.size(); i++) {
                String node = pathNodes.get(i);
                Point2D pt = nodePositions.get(node);
                int nx = (int) pt.getX();
                int ny = (int) pt.getY();

                Color nodeColor = NODE_DEFAULT_COLOR;
                if (node.equals(rc.start)) nodeColor = NODE_START_COLOR;
                else if (node.equals(rc.destination)) nodeColor = NODE_END_COLOR;
                else if (rc.waypoints.contains(node)) nodeColor = NODE_WAYPOINT_COLOR;

                g2.setPaint(new RadialGradientPaint(pt, nodeRadius * 1.5f, new float[]{0.0f, 1.0f}, new Color[]{new Color(0,0,0,180), new Color(0,0,0,0)}));
                g2.fill(new Ellipse2D.Double(nx - nodeRadius * 1.3, ny - nodeRadius * 1.3, nodeRadius * 2.6, nodeRadius * 2.6));

                g2.setColor(COLOR_CARD);
                g2.fill(new Ellipse2D.Double(nx - nodeRadius, ny - nodeRadius, nodeRadius * 2, nodeRadius * 2));

                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(nodeColor);
                g2.draw(new Ellipse2D.Double(nx - nodeRadius, ny - nodeRadius, nodeRadius * 2, nodeRadius * 2));

                g2.setColor(nodeColor);
                g2.fill(new Ellipse2D.Double(nx - 3, ny - 3, 6, 6));

                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tx = nx - fm.stringWidth(node) / 2;
                int ty = ny + nodeRadius + 18;

                g2.setColor(new Color(11, 15, 26, 200));
                g2.fillRect(tx - 4, ty - fm.getAscent() - 2, fm.stringWidth(node) + 8, fm.getHeight() + 4);

                g2.setColor(nodeColor);
                g2.drawString(node, tx, ty);
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
            g2.setColor(new Color(255, 255, 255, 12));
            g2.drawString(rc.caseName.toUpperCase(), 30, h - 40);
        }

        private Point2D calculateCoordinates(String node, int index, int total, int width, int height) {
            int padding = 80;
            int availableW = width - padding * 2;
            int availableH = height - padding * 2;
            int idNum = 0;
            try {
                idNum = Integer.parseInt(node.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                idNum = Math.abs(node.hashCode());
            }
            int segments = 3;
            int nodesPerSegment = (total + segments - 1) / segments;
            if (nodesPerSegment == 0) nodesPerSegment = 1;
            int row = index / nodesPerSegment;
            int col = index % nodesPerSegment;
            if (row % 2 != 0) col = nodesPerSegment - 1 - col;
            double x = padding + (availableW / (double) Math.max(1, nodesPerSegment - 1)) * col;
            double y = padding + (availableH / (double) Math.max(1, segments - 1)) * row;
            x += (idNum % 25) - 12.5;
            y += (idNum % 21) - 10.5;
            return new Point2D.Double(x, y);
        }

        private void drawDynamicArrow(Graphics2D g2, Point2D from, Point2D to, double tipPosFraction) {
            double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
            int arrowLength = 16;
            double arrowAngle = Math.PI / 5.0;

            int tipX = (int) (from.getX() + (to.getX() - from.getX()) * tipPosFraction);
            int tipY = (int) (from.getY() + (to.getY() - from.getY()) * tipPosFraction);

            Path2D.Double arrowHead = new Path2D.Double();
            arrowHead.moveTo(tipX, tipY);
            arrowHead.lineTo(tipX - arrowLength * Math.cos(angle - arrowAngle), tipY - arrowLength * Math.sin(angle - arrowAngle));
            arrowHead.lineTo(tipX - arrowLength * Math.cos(angle + arrowAngle), tipY - arrowLength * Math.sin(angle + arrowAngle));
            arrowHead.closePath();

            g2.setColor(NEON_START);
            g2.fill(arrowHead);
        }
    }

    private Set<UnorderedPair<String>> findBidirectionalSegments(List<String> pathNodes) {
        Set<UnorderedPair<String>> bidirectionalSegments = new HashSet<>();
        Set<Pair<String>> traversedForward = new HashSet<>();

        for (int i = 0; i < pathNodes.size() - 1; i++) {
            String n1 = pathNodes.get(i);
            String n2 = pathNodes.get(i + 1);
            traversedForward.add(new Pair<>(n1, n2));
            if (traversedForward.contains(new Pair<>(n2, n1))) {
                bidirectionalSegments.add(new UnorderedPair<>(n1, n2));
            }
        }
        return bidirectionalSegments;
    }

    class Pair<T> {
        public T p1; public T p2;
        public Pair(T p1, T p2) { this.p1 = p1; this.p2 = p2; }
        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair<?> other = (Pair<?>) obj;
            return p1.equals(other.p1) && p2.equals(other.p2);
        }
        @Override public int hashCode() { return p1.hashCode() ^ p2.hashCode(); }
    }

    class UnorderedPair<T> {
        public T p1; public T p2;
        public UnorderedPair(T p1, T p2) { this.p1 = p1; this.p2 = p2; }
        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            UnorderedPair<?> other = (UnorderedPair<?>) obj;
            return (p1.equals(other.p1) && p2.equals(other.p2)) ||
                    (p1.equals(other.p2) && p2.equals(other.p1));
        }
        @Override public int hashCode() { return p1.hashCode() ^ p2.hashCode(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<PathCase> mockList = new ArrayList<>();

            List<String> p1 = List.of("L0001");
            List<String> p2 = List.of("L0001", "L0340", "L0339", "L0895", "L0894", "L0082", "L0284", "L0010");
            List<String> p3 = List.of("L0001", "L0340", "L0339", "L0247", "L0017", "L0128", "L0107", "L0106", "L0105", "L0106", "L0107", "L0108", "L0827", "L0996", "L0101");
            List<String> p4 = List.of("L0001", "L0340", "L0339", "L0247", "L0017", "L0128", "L0107", "L0106", "L0105", "L0243", "L0242", "L0241", "L0385", "L0205", "L0201");

            mockList.add(new PathCase("Case 1: Self Test", "L0001", "L0001", List.of(), new PathResult(p1, 0.0)));
            mockList.add(new PathCase("Case 2: Direct Path", "L0001", "L0010", List.of(), new PathResult(p2, 27.0)));
            mockList.add(new PathCase("Case 3: Single Waypoint", "L0001", "L0101", List.of("L0105"), new PathResult(p3, 39.0)));
            mockList.add(new PathCase("Case 4: Ordered Multi-Waypoints", "L0001", "L0201", List.of("L0105", "L0205"), new PathResult(p4, 48.0)));

            PathVisualizer center = new PathVisualizer(mockList);
            center.setVisible(true);
        });
    }
}