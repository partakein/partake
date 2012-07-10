package in.partake.model.dao;

import java.util.Date;

import play.Logger;

public abstract class PartakeConnectionPool {
    // 同じ thread が複数の connection を取ると deadlock の可能性がある。その場合には警告されるため、修正すること。
    private ThreadLocal<Integer> numAcquiredConnection;
    private ThreadLocal<String>  firstConnectionName;

    protected PartakeConnectionPool() {
        this.numAcquiredConnection = new ThreadLocal<Integer>();
        this.firstConnectionName = new ThreadLocal<String>();
    }

    public int getCurrentNumberOfConnectionForThisThread() {
        Integer numConnection = numAcquiredConnection.get();
        if (numConnection == null)
            return 0;
        else
            return numConnection;
    }

    /** Connection を得る */
    public final PartakeConnection getConnection() throws DAOException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        String name = "unknown";
        if (stackTrace != null && stackTrace.length > 3) {
            name = stackTrace[3].getClassName() + "#" + stackTrace[3].getMethodName();
        }

        if (numAcquiredConnection.get() == null) {
            numAcquiredConnection.set(new Integer(1));
        } else {
            numAcquiredConnection.set(numAcquiredConnection.get() + 1);
        }
        if (numAcquiredConnection.get() > 1) {
            Logger.warn(name + " : The same thread has taken multiple connections. This may cause a bug. ");
            if (firstConnectionName.get() != null) {
                Logger.warn(firstConnectionName.get() + " is the first connection.");
            }
        }
        if (firstConnectionName.get() == null) {
            firstConnectionName.set(name);
        }

        Logger.debug("borrowing... " + name + " : " + numAcquiredConnection.get());
        return getConnectionImpl(name);
    }

    protected abstract PartakeConnection getConnectionImpl(String name) throws DAOException;

    /** Connection を返す */
    public final void releaseConnection(PartakeConnection connection) {
        int tenSeconds = 1000 * 10;
        Date now = new Date();

        if (connection.getAcquiredTime() + tenSeconds < now.getTime()) {
            Logger.warn("connection [" + connection.getName() + "] have been acquired for " + (now.getTime() - connection.getAcquiredTime()) + " milliseconds.");
        }

        numAcquiredConnection.set(numAcquiredConnection.get() - 1);
        if (numAcquiredConnection.get() == 0) {
            firstConnectionName.set(null);
        }

        Logger.debug("releasing... " + connection.getName() + " : " + numAcquiredConnection.get());
        releaseConnectionImpl(connection);
    }

    protected abstract void releaseConnectionImpl(PartakeConnection connection);

    /** この Connection Pool が不要になる直前に呼ばれる。必要に応じてコネクションプールを解放する。*/
    public abstract void willDestroy();
}
