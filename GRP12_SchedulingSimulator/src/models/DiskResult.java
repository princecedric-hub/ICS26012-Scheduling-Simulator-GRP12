package models;

import java.util.List;

public class DiskResult {
    public final List<Integer> seekSequence;   // Starts with initial head position
    public final List<Integer> stepMovements;  // Absolute distance moved per step
    public final int totalHeadMovement;
    public final double averageSeekDistance;

    public DiskResult(List<Integer> seekSequence, List<Integer> stepMovements,
                      int totalHeadMovement, double averageSeekDistance) {
        this.seekSequence = seekSequence;
        this.stepMovements = stepMovements;
        this.totalHeadMovement = totalHeadMovement;
        this.averageSeekDistance = averageSeekDistance;
    }
}