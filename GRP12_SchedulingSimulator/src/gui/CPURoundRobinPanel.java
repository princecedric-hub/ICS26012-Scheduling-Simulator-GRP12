package gui;

import algorithms.CPURoundRobin;
import interfaces.CPUScheduler;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import models.CPUProcess;
import models.CPUResult;
import models.GanttBlock;

public class CPURoundRobinPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient CPUScheduler scheduler = new CPURoundRobin();
    private final transient Runnable onBackToMenu;

    private JSpinner processCountSpinner;
    private JTextField quantumField;
    private DefaultTableModel inputTableModel;
    private JTable inputTable;

    private DefaultTableModel outputTableModel;
    private JTable outputTable;
    private GanttChartPanel ganttChartPanel;

    private JLabel avgWaitLabel;
    private JLabel avgTurnaroundLabel;
    private JLabel avgResponseLabel;
    private JLabel cpuUtilLabel;
    private JLabel throughputLabel;

    public CPURoundRobinPanel() {
        this(null);
    }

    public CPURoundRobinPanel(Runnable onBackToMenu) {
        this.onBackToMenu = onBackToMenu;
        setLayout(new BorderLayout(10, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        add(buildTopHeader(), BorderLayout.NORTH);
        add(buildCenterContent(), BorderLayout.CENTER);
        add(buildSummaryFooter(), BorderLayout.SOUTH);

        loadSampleTenProcesses();
    }

    private JPanel buildTopHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 6));

        JLabel title = new JLabel("CPU Scheduling — Preemptive Round Robin");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        header.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));

        controls.add(new JLabel("Processes:"));
        processCountSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        processCountSpinner.setPreferredSize(new Dimension(55, 26));
        processCountSpinner.addChangeListener(e -> resizeInputRows((Integer) processCountSpinner.getValue()));
        controls.add(processCountSpinner);

        controls.add(new JLabel("Time Quantum:"));
        quantumField = new JTextField("3", 4);
        quantumField.setPreferredSize(new Dimension(48, 26));
        controls.add(quantumField);

        JButton simulateBtn = new JButton("Simulate");
        simulateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        simulateBtn.addActionListener(e -> runSimulation());
        controls.add(simulateBtn);

        JButton sampleBtn = new JButton("Load Sample Data");
        sampleBtn.addActionListener(e -> loadSampleTenProcesses());
        controls.add(sampleBtn);

        JButton clearBtn = new JButton("Reset / Clear");
        clearBtn.addActionListener(e -> clearInputs());
        controls.add(clearBtn);

        if (onBackToMenu != null) {
            JButton backBtn = new JButton("Back to Menu");
            backBtn.addActionListener(e -> onBackToMenu.run());
            controls.add(backBtn);
        }

        header.add(controls, BorderLayout.CENTER);
        return header;
    }

    private JSplitPane buildCenterContent() {
        String[] inputCols = {"PID", "Arrival Time", "Burst Time"};
        inputTableModel = new DefaultTableModel(inputCols, 0);
        inputTable = new JTable(inputTableModel);
        inputTable.setRowHeight(24);
        inputTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JScrollPane inputScroll = new JScrollPane(inputTable);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Process Inputs (Supports 10+ Processes)"));
        inputScroll.setPreferredSize(new Dimension(285, 420));

        ganttChartPanel = new GanttChartPanel();
        JScrollPane ganttScroll = new JScrollPane(
                ganttChartPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        ganttScroll.setBorder(BorderFactory.createTitledBorder("Gantt Chart (Time Scale & Idle Time)"));
        ganttScroll.setPreferredSize(new Dimension(580, 150));

        String[] outputCols = {
            "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting", "Response"
        };
        outputTableModel = new DefaultTableModel(outputCols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        outputTable = new JTable(outputTableModel);
        outputTable.setRowHeight(24);

        JScrollPane outputScroll = new JScrollPane(outputTable);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Process Execution Metrics"));

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.add(ganttScroll, BorderLayout.NORTH);
        rightPanel.add(outputScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputScroll, rightPanel);
        splitPane.setDividerLocation(285);
        return splitPane;
    }

    private JPanel buildSummaryFooter() {
        JPanel footer = new JPanel(new GridLayout(1, 5, 6, 4));
        footer.setBorder(BorderFactory.createTitledBorder("Performance Summary"));

        avgWaitLabel = createMetricLabel("Avg Waiting: —");
        avgTurnaroundLabel = createMetricLabel("Avg Turnaround: —");
        avgResponseLabel = createMetricLabel("Avg Response: —");
        cpuUtilLabel = createMetricLabel("CPU Utilization: —");
        throughputLabel = createMetricLabel("Throughput: —");

        footer.add(avgWaitLabel);
        footer.add(avgTurnaroundLabel);
        footer.add(avgResponseLabel);
        footer.add(cpuUtilLabel);
        footer.add(throughputLabel);

        return footer;
    }

    private JLabel createMetricLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    private void resizeInputRows(int targetCount) {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        int currentRows = inputTableModel.getRowCount();
        while (currentRows < targetCount) {
            currentRows++;
            inputTableModel.addRow(new Object[]{"P" + currentRows, 0, 1});
        }
        while (currentRows > targetCount) {
            currentRows--;
            inputTableModel.removeRow(currentRows);
        }
    }

    private void loadSampleTenProcesses() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        inputTableModel.setRowCount(0);
        int[][] sampleData = {
            {0, 5}, {1, 3}, {2, 8}, {3, 6}, {4, 2},
            {6, 4}, {7, 7}, {9, 3}, {25, 4}, {27, 5}
        };
        for (int i = 0; i < sampleData.length; i++) {
            inputTableModel.addRow(new Object[]{
                "P" + (i + 1), sampleData[i][0], sampleData[i][1]
            });
        }
        processCountSpinner.setValue(10);
        quantumField.setText("3");
    }

    private void clearInputs() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        int count = (Integer) processCountSpinner.getValue();
        inputTableModel.setRowCount(0);
        for (int i = 1; i <= count; i++) {
            inputTableModel.addRow(new Object[]{"P" + i, "", ""});
        }
        outputTableModel.setRowCount(0);
        ganttChartPanel.setBlocks(Collections.emptyList());
        avgWaitLabel.setText("Avg Waiting: —");
        avgTurnaroundLabel.setText("Avg Turnaround: —");
        avgResponseLabel.setText("Avg Response: —");
        cpuUtilLabel.setText("CPU Utilization: —");
        throughputLabel.setText("Throughput: —");
    }

    private void runSimulation() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }

        int timeQuantum;
        try {
            timeQuantum = Integer.parseInt(quantumField.getText().trim());
            if (timeQuantum <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive integer for Time Quantum.",
                    "Invalid Time Quantum",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<CPUProcess> processes = new ArrayList<>();
        int rowCount = inputTableModel.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            Object pidObj = inputTableModel.getValueAt(r, 0);
            Object atObj = inputTableModel.getValueAt(r, 1);
            Object btObj = inputTableModel.getValueAt(r, 2);

            String pid = pidObj != null ? pidObj.toString().trim() : "";
            if (pid.isEmpty()) {
                pid = "P" + (r + 1);
            }

            try {
                int at = Integer.parseInt(atObj.toString().trim());
                int bt = Integer.parseInt(btObj.toString().trim());
                if (at < 0 || bt <= 0) {
                    throw new NumberFormatException();
                }
                processes.add(new CPUProcess(pid, at, bt));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Row " + (r + 1) + " (" + pid + "): Arrival Time must be >= 0 and Burst Time must be > 0.",
                        "Invalid Process Input",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        CPUResult result = scheduler.schedule(processes, timeQuantum);
        displayResults(result);
    }

    private void displayResults(CPUResult result) {
        outputTableModel.setRowCount(0);
        for (CPUProcess p : result.getProcesses()) {
            outputTableModel.addRow(new Object[]{
                p.getPid(),
                p.getArrivalTime(),
                p.getBurstTime(),
                p.getCompletionTime(),
                p.getTurnaroundTime(),
                p.getWaitingTime(),
                p.getResponseTime()
            });
        }

        ganttChartPanel.setBlocks(result.getGanttBlocks());

        avgWaitLabel.setText(String.format("Avg Waiting: %.2f", result.getAverageWaitingTime()));
        avgTurnaroundLabel.setText(String.format("Avg Turnaround: %.2f", result.getAverageTurnaroundTime()));
        avgResponseLabel.setText(String.format("Avg Response: %.2f", result.getAverageResponseTime()));
        cpuUtilLabel.setText(String.format("CPU Utilization: %.2f%%", result.getCpuUtilization()));
        throughputLabel.setText(String.format("Throughput: %.4f / unit", result.getThroughput()));
    }

    private static class GanttChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private static final Color[] PALETTE = {
            new Color(59, 130, 246),
            new Color(16, 185, 129),
            new Color(245, 158, 11),
            new Color(139, 92, 246),
            new Color(236, 72, 153),
            new Color(20, 184, 166),
            new Color(249, 115, 22),
            new Color(99, 102, 241),
            new Color(34, 197, 94),
            new Color(239, 68, 68)
        };

        private transient List<GanttBlock> blocks = Collections.emptyList();
        private final Map<String, Color> colorMap = new HashMap<>();

        public GanttChartPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(560, 110));
        }

        public void setBlocks(List<GanttBlock> blocks) {
            this.blocks = blocks != null ? blocks : Collections.emptyList();
            this.colorMap.clear();
            int idx = 0;
            for (GanttBlock b : this.blocks) {
                if (!b.isIdle() && !colorMap.containsKey(b.getLabel())) {
                    colorMap.put(b.getLabel(), PALETTE[idx % PALETTE.length]);
                    idx++;
                }
            }
            int totalUnits = this.blocks.isEmpty() ? 0 : this.blocks.get(this.blocks.size() - 1).getEndTime();
            int dynamicWidth = Math.max(560, 60 + totalUnits * 28);
            setPreferredSize(new Dimension(dynamicWidth, 110));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (blocks.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("Click 'Simulate' to generate the Round Robin Gantt chart.", 20, 55);
                g2.dispose();
                return;
            }

            int leftMargin = 25;
            int rightMargin = 25;
            int barY = 20;
            int barHeight = 44;
            int totalTime = blocks.get(blocks.size() - 1).getEndTime();
            int usableWidth = Math.max(getWidth() - leftMargin - rightMargin, totalTime * 24);
            double pixelsPerUnit = (double) usableWidth / totalTime;

            Font labelFont = new Font("SansSerif", Font.BOLD, 12);
            Font timeFont = new Font("SansSerif", Font.PLAIN, 11);
            FontMetrics labelFm = g2.getFontMetrics(labelFont);
            FontMetrics timeFm = g2.getFontMetrics(timeFont);

            for (int i = 0; i < blocks.size(); i++) {
                GanttBlock b = blocks.get(i);
                int x1 = leftMargin + (int) Math.round(b.getStartTime() * pixelsPerUnit);
                int x2 = leftMargin + (int) Math.round(b.getEndTime() * pixelsPerUnit);
                int w = Math.max(x2 - x1, 18);

                if (b.isIdle()) {
                    g2.setColor(new Color(229, 231, 235));
                    g2.fillRect(x1, barY, w, barHeight);
                    g2.setColor(new Color(107, 114, 128));
                } else {
                    g2.setColor(colorMap.get(b.getLabel()));
                    g2.fillRect(x1, barY, w, barHeight);
                    g2.setColor(Color.WHITE);
                }

                g2.setFont(labelFont);
                String text = b.getLabel();
                int textX = x1 + (w - labelFm.stringWidth(text)) / 2;
                int textY = barY + (barHeight + labelFm.getAscent()) / 2 - 2;
                g2.drawString(text, Math.max(x1 + 2, textX), textY);

                g2.setColor(new Color(55, 65, 81));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRect(x1, barY, w, barHeight);

                g2.setFont(timeFont);
                String startStr = String.valueOf(b.getStartTime());
                g2.drawString(startStr, x1 - timeFm.stringWidth(startStr) / 2, barY + barHeight + 18);

                if (i == blocks.size() - 1) {
                    String endStr = String.valueOf(b.getEndTime());
                    g2.drawString(endStr, x2 - timeFm.stringWidth(endStr) / 2, barY + barHeight + 18);
                }
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GRP12 — CPU Scheduling (Round Robin)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CPURoundRobinPanel());
            frame.setSize(1080, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}