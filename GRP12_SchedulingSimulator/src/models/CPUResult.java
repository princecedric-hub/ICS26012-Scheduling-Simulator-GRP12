package models;

import java.util.List;

public class CPUResult {
    private final List<CPUProcess> processes;
    private final List<GanttBlock> ganttBlocks;
    private final int totalElapsedTime;
    private final int totalIdleTime;
    private final double averageWaitingTime;
    private final double averageTurnaroundTime;
    private final double averageResponseTime;
    private final double cpuUtilization;
    private final double throughput;

    public CPUResult(
            List<CPUProcess> processes,
            List<GanttBlock> ganttBlocks,
            int totalElapsedTime,
            int totalIdleTime,
            double averageWaitingTime,
            double averageTurnaroundTime,
            double averageResponseTime,
            double cpuUtilization,
            double throughput) {
        this.processes = processes;
        this.ganttBlocks = ganttBlocks;
        this.totalElapsedTime = totalElapsedTime;
        this.totalIdleTime = totalIdleTime;
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        this.averageResponseTime = averageResponseTime;
        this.cpuUtilization = cpuUtilization;
        this.throughput = throughput;
    }

    public List<CPUProcess> getProcesses() { return processes; }
    public List<GanttBlock> getGanttBlocks() { return ganttBlocks; }
    public int getTotalElapsedTime() { return totalElapsedTime; }
    public int getTotalIdleTime() { return totalIdleTime; }
    public double getAverageWaitingTime() { return averageWaitingTime; }
    public double getAverageTurnaroundTime() { return averageTurnaroundTime; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public double getCpuUtilization() { return cpuUtilization; }
    public double getThroughput() { return throughput; }
}