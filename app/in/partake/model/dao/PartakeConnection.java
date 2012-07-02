package in.partake.model.dao;

import org.apache.log4j.Logger;

public abstract class PartakeConnection {
    private static final Logger logger = Logger.getLogger(PartakeConnection.class);

    private String name;
    private PartakeConnectionPool pool;
    private long acquiredTime;

    private boolean invalidated;

    protected PartakeConnection(String name, PartakeConnectionPool pool, long acquiredTime) {
        this.name = name;
        this.pool = pool;
        this.acquiredTime = acquiredTime;
        this.invalidated = false;
    }

    public String getName() {
        return name;
    }

    public long getAcquiredTime() {
        return acquiredTime;
    }

    /**
     * this method has 2 tasks to do:
     * <ul>
     * <li>Release connection.
     * <li>Rollback the transaction on this connection
     * if transaction is enable and still active.
     * </ul>
     */
    public synchronized void invalidate() {
        if (isInTransaction())
            logger.error("You called invalidate() for connection being in connection.");
        if (invalidated)
            logger.error("You called invalidate() for already invalidated connection.");

        this.invalidated = true;
        pool.releaseConnection(this);
    }

    @Override
    // サブクラスに上書きされて実行されなくなる、なんてことがないようにfinalで修飾している。
    // もしサブクラスでもfinalizeを実装したいなんて残念なことになったら、このクラスにFinalizer Guardianを使うこと。
    protected final void finalize() throws Throwable {
        if (!invalidated) {
            logger.error("RESOURCE LEAK! : Connection [" + getName() + "] was not invalidated.");
            invalidate();
        }

        super.finalize();
    }

    public abstract void beginTransaction() throws DAOException;
    public abstract void commit() throws DAOException;
    public abstract void rollback() throws DAOException;
    public abstract boolean isInTransaction();
}

