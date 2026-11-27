package models;

public class GanttBlock {
    private final String label; // Process PID (e.g., "P1") or "IDLE"
    private final int startTime;
    private final int endTime;
    private final boolean idle;

    public GanttBlock(String label, int startTime, int endTime, boolean idle) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.idle = idle;
    }

    public String getLabel() { return label; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
    public boolean isIdle() { return idle; }
    public int getDuration() { return endTime - startTime; }
}
