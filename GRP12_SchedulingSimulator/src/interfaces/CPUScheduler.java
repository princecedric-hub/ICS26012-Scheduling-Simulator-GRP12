package interfaces;

import java.util.List;
import models.CPUProcess;
import models.CPUResult;

public interface CPUScheduler {
    CPUResult schedule(List<CPUProcess> processes, int timeQuantum);
}