package in.partake.daemon.impl;

import in.partake.daemon.PartakeDaemon;
import in.partake.service.IDaemonInitializer;

public class DaemonInitializer implements IDaemonInitializer {

    @Override
    public void initialize() throws Exception {
        PartakeDaemon.getInstance().removeAllTasks();
        PartakeDaemon.getInstance().addTask(new EventReminderTask());
        PartakeDaemon.getInstance().addTask(new SendParticipationStatusChangeNotificationsTask());
        PartakeDaemon.getInstance().addTask(new SendMessageEnvelopeTask());
    }
}
