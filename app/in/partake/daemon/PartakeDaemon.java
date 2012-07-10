package in.partake.daemon;

import in.partake.app.PartakeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import play.Logger;

class PartakeDaemonTask extends TimerTask {
    private final PartakeDaemon daemon;

    public PartakeDaemonTask(PartakeDaemon daemon) {
        this.daemon = daemon;
    }

    @Override
    public void run() {
        if (!PartakeConfiguration.isTwitterDaemonEnabled()) {
            Logger.debug("Twitter daemon task is disabled.");
            return;
        }

        for (IPartakeDaemonTask task: daemon.getTasks()) {
            Logger.info(task.getName() + " will start...");
            try {
                task.run();
                Logger.info(task.getName() + " has finished without an error.");
            } catch (Exception e) {
                Logger.error(task.getName() + " has encountered an error. This should be immediately fixed.");
            }
        }
    }
}

public class PartakeDaemon {
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
        Logger.info("Daemons are scheduling...");
        // initial wait is required because application initialization might not be finished.
        timer.schedule(new PartakeDaemonTask(this), TIMER_INTERVAL_IN_MILLIS, TIMER_INTERVAL_IN_MILLIS);
    }

    public void cancel() {
        timer.cancel();
        Logger.info("Scheduled twitter daemons have been cancelled.");
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
