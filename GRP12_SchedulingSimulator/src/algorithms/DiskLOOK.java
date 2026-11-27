// Author: Prince Cedric D. Rolante
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiskLOOK {

    public static class Result {
        public final List<Integer> seekSequence;
        public final List<Integer> stepMovements;
        public final int totalHeadMovement;
        public final double averageSeekDistance;

        public Result(List<Integer> seekSequence, List<Integer> stepMovements,
                      int totalHeadMovement, double averageSeekDistance) {
            this.seekSequence = seekSequence;
            this.stepMovements = stepMovements;
            this.totalHeadMovement = totalHeadMovement;
            this.averageSeekDistance = averageSeekDistance;
        }
    }

    public Result schedule(int initialHead, int diskSize, List<Integer> requests, String direction) {
        List<Integer> sequence = new ArrayList<>();
        List<Integer> steps = new ArrayList<>();

        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        List<Integer> atHead = new ArrayList<>();

        for (int req : requests) {
            if (req < initialHead) {
                left.add(req);
            } else if (req > initialHead) {
                right.add(req);
            } else {
                atHead.add(req);
            }
        }

        // Sort left descending (closest to head first), right ascending (closest to head first)
        left.sort(Collections.reverseOrder());
        Collections.sort(right);

        List<Integer> orderedTargets = new ArrayList<>(atHead);
        if ("RIGHT".equalsIgnoreCase(direction)) {
            orderedTargets.addAll(right);
            orderedTargets.addAll(left);
        } else {
            orderedTargets.addAll(left);
            orderedTargets.addAll(right);
        }

        sequence.add(initialHead);
        int currentHead = initialHead;
        int totalMovement = 0;

        for (int target : orderedTargets) {
            int distance = Math.abs(target - currentHead);
            steps.add(distance);
            totalMovement += distance;
            sequence.add(target);
            currentHead = target;
        }

        double average = requests.isEmpty() ? 0.0 : (double) totalMovement / requests.size();
        return new Result(sequence, steps, totalMovement, average);
    }
}