package in.partake.daemon;

public interface IPartakeDaemonTask {
    public String getName();
    public void run() throws Exception;
}
