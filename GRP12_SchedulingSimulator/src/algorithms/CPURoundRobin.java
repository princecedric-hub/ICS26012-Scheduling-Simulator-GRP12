package algorithms;

import interfaces.CPUScheduler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import models.CPUProcess;
import models.CPUResult;
import models.GanttBlock;

public class CPURoundRobin implements CPUScheduler {

    @Override
    public CPUResult schedule(List<CPUProcess> inputProcesses, int timeQuantum) {
        if (inputProcesses == null || inputProcesses.isEmpty()) {
            throw new IllegalArgumentException("Process list cannot be empty.");
        }
        if (timeQuantum <= 0) {
            throw new IllegalArgumentException("Time quantum must be greater than 0.");
        }

        List<CPUProcess> displayOrder = new ArrayList<>();
        for (CPUProcess p : inputProcesses) {
            if (p.getArrivalTime() < 0) {
                throw new IllegalArgumentException("Arrival time cannot be negative for " + p.getPid());
            }
            if (p.getBurstTime() <= 0) {
                throw new IllegalArgumentException("Burst time must be positive for " + p.getPid());
            }
            displayOrder.add(p.copy());
        }

        List<CPUProcess> sortedByArrival = new ArrayList<>(displayOrder);
        sortedByArrival.sort(Comparator.comparingInt(CPUProcess::getArrivalTime));

        Deque<CPUProcess> readyQueue = new ArrayDeque<>();
        List<GanttBlock> ganttBlocks = new ArrayList<>();

        int n = sortedByArrival.size();
        int completedCount = 0;
        int nextArrivalIdx = 0;
        int currentTime = 0;
        int totalIdleTime = 0;
        int totalBurstTime = 0;

        for (CPUProcess p : displayOrder) {
            totalBurstTime += p.getBurstTime();
        }

        while (nextArrivalIdx < n && sortedByArrival.get(nextArrivalIdx).getArrivalTime() <= currentTime) {
            readyQueue.addLast(sortedByArrival.get(nextArrivalIdx));
            nextArrivalIdx++;
        }

        while (completedCount < n) {
            if (readyQueue.isEmpty()) {
                int nextArrivalTime = sortedByArrival.get(nextArrivalIdx).getArrivalTime();
                if (nextArrivalTime > currentTime) {
                    ganttBlocks.add(new GanttBlock("IDLE", currentTime, nextArrivalTime, true));
                    totalIdleTime += (nextArrivalTime - currentTime);
                    currentTime = nextArrivalTime;
                }
                while (nextArrivalIdx < n && sortedByArrival.get(nextArrivalIdx).getArrivalTime() <= currentTime) {
                    readyQueue.addLast(sortedByArrival.get(nextArrivalIdx));
                    nextArrivalIdx++;
                }
            }

            CPUProcess current = readyQueue.pollFirst();

            if (current.getFirstStartTime() == -1) {
                current.setFirstStartTime(currentTime);
            }

            int execSlice = Math.min(timeQuantum, current.getRemainingTime());
            int sliceStart = currentTime;
            int sliceEnd = currentTime + execSlice;

            ganttBlocks.add(new GanttBlock(current.getPid(), sliceStart, sliceEnd, false));

            currentTime = sliceEnd;
            current.setRemainingTime(current.getRemainingTime() - execSlice);

            // Enqueue new arrivals at currentTime BEFORE re-queueing the preempted process
            while (nextArrivalIdx < n && sortedByArrival.get(nextArrivalIdx).getArrivalTime() <= currentTime) {
                readyQueue.addLast(sortedByArrival.get(nextArrivalIdx));
                nextArrivalIdx++;
            }

            if (current.getRemainingTime() > 0) {
                readyQueue.addLast(current);
            } else {
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                current.setResponseTime(current.getFirstStartTime() - current.getArrivalTime());
                completedCount++;
            }
        }

        double sumWT = 0.0;
        double sumTAT = 0.0;
        double sumRT = 0.0;
        for (CPUProcess p : displayOrder) {
            sumWT += p.getWaitingTime();
            sumTAT += p.getTurnaroundTime();
            sumRT += p.getResponseTime();
        }

        int totalElapsedTime = currentTime;
        double avgWT = sumWT / n;
        double avgTAT = sumTAT / n;
        double avgRT = sumRT / n;
        double cpuUtilization = totalElapsedTime > 0
                ? ((double) totalBurstTime / totalElapsedTime) * 100.0
                : 0.0;
        double throughput = totalElapsedTime > 0
                ? (double) n / totalElapsedTime
                : 0.0;

        return new CPUResult(
                displayOrder,
                ganttBlocks,
                totalElapsedTime,
                totalIdleTime,
                avgWT,
                avgTAT,
                avgRT,
                cpuUtilization,
                throughput
        );
    }
}