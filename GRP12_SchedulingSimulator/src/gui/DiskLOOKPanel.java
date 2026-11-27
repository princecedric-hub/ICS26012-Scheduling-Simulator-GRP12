// Author: Prince Cedric D. Rolante
package gui;

import algorithms.DiskLOOK;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class DiskLOOKPanel extends JPanel {

    private final JTextField txtDiskSize;
    private final JTextField txtInitialHead;
    private final JComboBox<String> cmbDirection;
    private final JTextField txtRequests;
    private final JTextArea txtOutput;
    private final HeadPathGraphPanel graphPanel;
    private final DiskLOOK scheduler = new DiskLOOK();

    public DiskLOOKPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- TOP: INPUT PANEL ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("Disk Scheduling - LOOK Inputs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Disk Size (N, cylinders 0 to N-1):"), gbc);
        gbc.gridx = 1;
        txtDiskSize = new JTextField("200", 10);
        inputPanel.add(txtDiskSize, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Initial Head Position:"), gbc);
        gbc.gridx = 1;
        txtInitialHead = new JTextField(10);
        inputPanel.add(txtInitialHead, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Initial Direction:"), gbc);
        gbc.gridx = 1;
        cmbDirection = new JComboBox<>(new String[]{"RIGHT", "LEFT"});
        inputPanel.add(cmbDirection, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Request Queue (comma-separated, 20+ supported):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRequests = new JTextField(35);
        inputPanel.add(txtRequests, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnCalculate = new JButton("Simulate LOOK");
        JButton btnSample = new JButton("Load Sample Data");
        JButton btnClear = new JButton("Reset / Clear");

        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnSample);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER: SPLIT OUTPUT TEXT & PATH GRAPH ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.45);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollOutput = new JScrollPane(txtOutput);
        scrollOutput.setBorder(new TitledBorder("Computation Results"));
        splitPane.setLeftComponent(scrollOutput);

        graphPanel = new HeadPathGraphPanel();
        graphPanel.setBorder(new TitledBorder("Read/Write Head Path Graph"));
        splitPane.setRightComponent(graphPanel);

        add(splitPane, BorderLayout.CENTER);

        // --- BUTTON LISTENERS ---
        btnCalculate.addActionListener(e -> runSimulation());
        btnSample.addActionListener(e -> loadSampleData());
        btnClear.addActionListener(e -> resetFields());
    }

    private void loadSampleData() {
        txtDiskSize.setText("200");
        txtInitialHead.setText("53");
        cmbDirection.setSelectedItem("RIGHT");
        txtRequests.setText("98, 183, 37, 122, 14, 124, 65, 67");
        runSimulation();
    }

    private void resetFields() {
        txtDiskSize.setText("200");
        txtInitialHead.setText("");
        cmbDirection.setSelectedIndex(0);
        txtRequests.setText("");
        txtOutput.setText("");
        graphPanel.clearGraph();
    }

    private void runSimulation() {
        try {
            String sizeStr = txtDiskSize.getText().trim();
            if (sizeStr.isEmpty()) {
                showError("Disk Size cannot be empty.");
                return;
            }
            int diskSize = Integer.parseInt(sizeStr);
            if (diskSize <= 1) {
                showError("Disk Size (N) must be at least 2 (cylinders 0 to N-1).");
                return;
            }

            String headStr = txtInitialHead.getText().trim();
            if (headStr.isEmpty()) {
                showError("Initial Head Position cannot be empty.");
                return;
            }
            int initialHead = Integer.parseInt(headStr);
            if (initialHead < 0 || initialHead >= diskSize) {
                showError("Initial Head Position must be between 0 and " + (diskSize - 1) + ".");
                return;
            }

            String reqStr = txtRequests.getText().trim();
            if (reqStr.isEmpty()) {
                showError("Request Queue cannot be empty.");
                return;
            }
            String[] tokens = reqStr.split("[,\\s]+");
            List<Integer> requests = new ArrayList<>();
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                int cyl = Integer.parseInt(token);
                if (cyl < 0 || cyl >= diskSize) {
                    showError("Invalid cylinder request '" + cyl + "'. Must be between 0 and " + (diskSize - 1) + ".");
                    return;
                }
                requests.add(cyl);
            }
            if (requests.isEmpty()) {
                showError("Please enter at least one valid cylinder request.");
                return;
            }

            String direction = (String) cmbDirection.getSelectedItem();
            DiskLOOK.Result result = scheduler.schedule(initialHead, diskSize, requests, direction);
            displayResults(result, direction);
            graphPanel.setGraphData(result.seekSequence, diskSize);

        } catch (NumberFormatException ex) {
            showError("Invalid numeric input. Please enter whole numbers only.");
        }
    }

    private void displayResults(DiskLOOK.Result result, String direction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Initial Direction: ").append(direction).append("\n\n");
        sb.append("=== ORDER OF SERVICE (SEEK SEQUENCE) ===\n");
        for (int i = 0; i < result.seekSequence.size(); i++) {
            sb.append(result.seekSequence.get(i));
            if (i < result.seekSequence.size() - 1) sb.append(" -> ");
        }
        sb.append("\n\n=== HEAD MOVEMENT PER STEP ===\n");
        for (int i = 0; i < result.stepMovements.size(); i++) {
            int from = result.seekSequence.get(i);
            int to = result.seekSequence.get(i + 1);
            int dist = result.stepMovements.get(i);
            sb.append(String.format("Step %2d: |%3d - %3d| = %d cylinders\n", (i + 1), to, from, dist));
        }
        sb.append("\n=== SUMMARY METRICS ===\n");
        sb.append("Total Head Movement   : ").append(result.totalHeadMovement).append(" cylinders\n");
        sb.append(String.format("Average Seek Distance : %.2f cylinders\n", result.averageSeekDistance));

        txtOutput.setText(sb.toString());
        txtOutput.setCaretPosition(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    // --- CUSTOM SWING GRAPH FOR HEAD PATH ---
    private static class HeadPathGraphPanel extends JPanel {
        private List<Integer> sequence = new ArrayList<>();
        private int diskSize = 200;

        public HeadPathGraphPanel() {
            setBackground(Color.WHITE);
        }

        public void setGraphData(List<Integer> sequence, int diskSize) {
            this.sequence = sequence;
            this.diskSize = diskSize;
            repaint();
        }

        public void clearGraph() {
            this.sequence = new ArrayList<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sequence == null || sequence.size() < 2) {
                g.setColor(Color.GRAY);
                g.drawString("Run simulation to view the head path graph.", 20, 40);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padLeft = 40, padRight = 30, padTop = 45, padBottom = 30;
            int width = getWidth() - padLeft - padRight;
            int height = getHeight() - padTop - padBottom;
            if (width <= 0 || height <= 0) return;

            int maxCyl = diskSize - 1;

            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(padLeft, padTop, padLeft + width, padTop);
            g2.drawString("0", padLeft - 4, padTop - 10);
            g2.drawString(String.valueOf(maxCyl), padLeft + width - 15, padTop - 10);

            int steps = sequence.size();
            double yStep = (double) height / (steps - 1);

            int prevX = padLeft + (int) (((double) sequence.get(0) / maxCyl) * width);
            int prevY = padTop;

            for (int i = 0; i < steps; i++) {
                int cyl = sequence.get(i);
                int x = padLeft + (int) (((double) cyl / maxCyl) * width);
                int y = padTop + (int) (i * yStep);

                if (i > 0) {
                    g2.setColor(new Color(30, 110, 200));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawLine(prevX, prevY, x, y);
                }

                g2.setColor(i == 0 ? new Color(0, 150, 70) : new Color(200, 40, 40));
                g2.fillOval(x - 4, y - 4, 8, 8);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString(String.valueOf(cyl), x + 6, y + 4);

                prevX = x;
                prevY = y;
            }
        }
    }
}