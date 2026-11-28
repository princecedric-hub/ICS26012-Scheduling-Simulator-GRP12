package interfaces;

import java.util.List;
import models.DiskResult;

public interface DiskScheduler {
    DiskResult schedule(int initialHead, int diskSize, List<Integer> requests, String direction);
}