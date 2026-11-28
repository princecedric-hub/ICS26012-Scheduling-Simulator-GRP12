package models;

public class CPUProcess {
    private final String pid;
    private final int arrivalTime;
    private final int burstTime;

    private int remainingTime;
    private int firstStartTime = -1;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;

    public CPUProcess(String pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public CPUProcess copy() {
        return new CPUProcess(this.pid, this.arrivalTime, this.burstTime);
    }

    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public int getFirstStartTime() { return firstStartTime; }
    public void setFirstStartTime(int firstStartTime) { this.firstStartTime = firstStartTime; }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getResponseTime() { return responseTime; }
    public void setResponseTime(int responseTime) { this.responseTime = responseTime; }
}