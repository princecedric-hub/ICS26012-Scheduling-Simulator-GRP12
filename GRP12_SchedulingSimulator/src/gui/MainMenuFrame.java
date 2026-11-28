package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainMenuFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String CARD_CPU_RR = "CPU_ROUND_ROBIN";
    private static final String CARD_DISK_LOOK = "DISK_LOOK";

    private CardLayout cardLayout;
    private JPanel contentArea;

    public MainMenuFrame() {
        setTitle("ICS26012 - Group 12 CPU & Disk Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 680);
        setMinimumSize(new Dimension(1020, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);

        // Default view on startup
        cardLayout.show(contentArea, CARD_DISK_LOOK);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(265, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(14, 10, 12, 10));

        // --- CPU SCHEDULING SECTION ---
        sidebar.add(createSectionLabel("CPU SCHEDULING"));
        sidebar.add(Box.createVerticalStrut(6));

        JButton cpuPriorityBtn = createSidebarButton("1. CPU Non-Preemptive (Priority)");
        cpuPriorityBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Plug Teammate 1's CPU Panel here.",
                "Message",
                JOptionPane.INFORMATION_MESSAGE
            )
        );
        sidebar.add(cpuPriorityBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton cpuRoundRobinBtn = createSidebarButton("2. CPU Preemptive (Round Robin)");
        cpuRoundRobinBtn.addActionListener(e -> cardLayout.show(contentArea, CARD_CPU_RR));
        sidebar.add(cpuRoundRobinBtn);

        sidebar.add(Box.createVerticalStrut(20));

        // --- DISK SCHEDULING SECTION ---
        sidebar.add(createSectionLabel("DISK SCHEDULING"));
        sidebar.add(Box.createVerticalStrut(6));

        JButton diskLookBtn = createSidebarButton("3. Disk LOOK");
        diskLookBtn.addActionListener(e -> cardLayout.show(contentArea, CARD_DISK_LOOK));
        sidebar.add(diskLookBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton diskCLookBtn = createSidebarButton("4. Disk C-LOOK");
        diskCLookBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Plug Teammate 2's Disk C-LOOK Panel here.",
                "Message",
                JOptionPane.INFORMATION_MESSAGE
            )
        );
        sidebar.add(diskCLookBtn);

        // Push bottom buttons to the bottom of the sidebar
        sidebar.add(Box.createVerticalGlue());

        JButton aboutBtn = createSidebarButton("About Group");
        aboutBtn.addActionListener(e -> showAboutDialog());
        sidebar.add(aboutBtn);
        sidebar.add(Box.createVerticalStrut(6));

        JButton exitBtn = createSidebarButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        sidebar.add(exitBtn);

        return sidebar;
    }

    private JPanel buildContentArea() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        contentArea.add(new DiskLOOKPanel(), CARD_DISK_LOOK);
        contentArea.add(new CPURoundRobinPanel(), CARD_CPU_RR);

        return contentArea;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        button.setPreferredSize(new Dimension(245, 32));
        return button;
    }

    private void showAboutDialog() {
        String message =
            "ICS26012 - Operating Systems\n" +
            "CPU and Disk Scheduling Simulator\n\n" +
            "Group Number: Group 12\n" +
            "Members:\n" +
            "\u2022 [Prince Cedric D. Bolante] - Leader & Disk LOOK\n" +
            "\u2022 [Jeo Rivas] - Documentation Lead & CPU Preemptive (Round Robin)\n" +
            "\u2022 [Patrick Redor] - Video Lead & CPU Non-Preemptive (Priority)\n" +
            "\u2022 [Cid Stephen Concio] - Testing Lead & Disk C-LOOK";

        JOptionPane.showMessageDialog(
            this,
            message,
            "About Group 12",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame frame = new MainMenuFrame();
            frame.setVisible(true);
        });
    }
}