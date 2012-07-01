package in.partake.daemon;

import in.partake.resource.PartakeProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


class PartakeDaemonTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(PartakeDaemonTask.class);
    private final PartakeDaemon daemon;

    public PartakeDaemonTask(PartakeDaemon daemon) {
        this.daemon = daemon;
    }

    @Override
    public void run() {
        if (!PartakeProperties.get().isEnabledTwitterDaemon()) {
            logger.debug("Twitter daemon task is disabled.");
            return;
        }

        logger.info("Daemon task will start...");
        try {
            for (IPartakeDaemonTask task : daemon.getTasks())
                task.run();
        } catch (Exception e) {
            logger.warn("PartakeTask throws an exception", e);
        }
        logger.info("Daemon tasks have finished.");
    }
}

public class PartakeDaemon {
    private static final Logger logger = Logger.getLogger(PartakeDaemon.class);
    private static final int TIMER_INTERVAL_IN_MILLIS = 30000; // 30 secs. TODO: magic number!

    private static PartakeDaemon instance = new PartakeDaemon();
    private Timer timer;
    private List<IPartakeDaemonTask> tasks;

    public static PartakeDaemon getInstance() {
        return instance;
    }

    private PartakeDaemon() {
        timer = new Timer();
        tasks = new ArrayList<IPartakeDaemonTask>();
    }

    public void schedule() {
        logger.info("Daemons are scheduling...");
        // initial wait is required because application initialization might not be finished.
        timer.schedule(new PartakeDaemonTask(this), TIMER_INTERVAL_IN_MILLIS, TIMER_INTERVAL_IN_MILLIS);
    }

    public void cancel() {
        timer.cancel();
        logger.info("Scheduled twitter daemons have been cancelled.");
    }

    public void addTask(IPartakeDaemonTask task) {
        tasks.add(task);
    }

    public List<IPartakeDaemonTask> getTasks() {
        return tasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }
}
