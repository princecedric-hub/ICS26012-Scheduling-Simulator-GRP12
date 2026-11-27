// Author: Prince Cedric D. Bolante - Group 12 Leader
package gui;

import java.awt.*;
import javax.swing.*;

public class MainMenuFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MainMenuFrame() {
        setTitle("ICS26012 - Group 12 CPU & Disk Scheduling Simulator");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- LEFT NAVIGATION SIDEBAR ---
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        navPanel.setBackground(new Color(235, 240, 245));
        navPanel.setPreferredSize(new Dimension(230, getHeight()));

        JLabel lblCpu = new JLabel("CPU SCHEDULING");
        lblCpu.setFont(new Font("SansSerif", Font.BOLD, 12));
        navPanel.add(lblCpu);
        navPanel.add(Box.createVerticalStrut(8));

        JButton btnCpu1 = createMenuButton("1. CPU Non-Preemptive (Priority)");
        JButton btnCpu2 = createMenuButton("2. CPU Preemptive (Round Robin)");
        navPanel.add(btnCpu1);
        navPanel.setPreferredSize(new Dimension(260, getHeight()));
        navPanel.add(btnCpu2);

        navPanel.add(Box.createVerticalStrut(20));
        JLabel lblDisk = new JLabel("DISK SCHEDULING");
        lblDisk.setFont(new Font("SansSerif", Font.BOLD, 12));
        navPanel.add(lblDisk);
        navPanel.add(Box.createVerticalStrut(8));

        JButton btnDisk1 = createMenuButton("3. Disk LOOK");
        JButton btnDisk2 = createMenuButton("4. Disk C-LOOK");
        navPanel.add(btnDisk1);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnDisk2);

        navPanel.add(Box.createVerticalGlue());

        JButton btnAbout = createMenuButton("About Group");
        JButton btnExit = createMenuButton("Exit");
        navPanel.add(btnAbout);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(btnExit);

        add(navPanel, BorderLayout.WEST);

        // --- RIGHT CONTENT SCREENS ---
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.add(new JLabel("<html><center><h2>Group 12 - CPU & Disk Scheduling Simulator</h2>"
                + "<p>Select an algorithm from the left menu to begin.</p></center></html>"));

        contentPanel.add(welcomePanel, "HOME");
        contentPanel.add(new DiskLOOKPanel(), "DISK_LOOK");

        add(contentPanel, BorderLayout.CENTER);

        // --- NAVIGATION ACTIONS ---
        btnDisk1.addActionListener(e -> cardLayout.show(contentPanel, "DISK_LOOK"));
        btnCpu1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Plug Teammate 1's CPU Panel here."));
        btnCpu2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Plug Teammate 2's CPU Panel here."));
        btnDisk2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Plug Teammate 3's Disk Panel here."));

        btnAbout.addActionListener(e -> showAboutDialog());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void showAboutDialog() {
        String info = "ICS26012 - Operating Systems\n"
                + "CPU and Disk Scheduling Simulator\n\n"
                + "Group Number: Group 12\n"
                + "Members:\n"
                + "• [Prince Cedric D. Bolante] - Leader & Disk LOOK\n"
                + "• [Jeo Rivas] - Documentation Lead & CPU Preemptive (Round Robin)\n"
                + "• [Patrick Redor] - Video Lead & CPU Non-Preemptive (Priority)\n"
                + "• [Cid Stephen Concio] - Testing Lead & Disk C-LOOK";
        JOptionPane.showMessageDialog(this, info, "About Group 12", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}