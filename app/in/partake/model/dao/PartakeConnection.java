package in.partake.model.dao;

import org.apache.log4j.Logger;

public abstract class PartakeConnection {
    private static final Logger logger = Logger.getLogger(PartakeConnection.class);

    private String name;
    private PartakeConnectionPool pool;
    private long acquiredTime;

    private int refCount; // for debugging.

    protected PartakeConnection(String name, PartakeConnectionPool pool, long acquiredTime) {
        this.name = name;
        this.pool = pool;
        this.acquiredTime = acquiredTime;

        this.refCount = 1;
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
        --refCount;

        if (refCount == 0) {
            pool.releaseConnection(this);
        } else if (refCount < 0) {
            logger.error("invalidate() called too much!");
            throw new IllegalStateException("invalidate() called too much");
        }
    }

    public synchronized void retain() {
        if (refCount <= 0) {
            throw new IllegalStateException("refCount should not be less than or equal to 0 to retain the connection.");
        }

        ++refCount;
    }

    @Override
    // サブクラスに上書きされて実行されなくなる、なんてことがないようにfinalで修飾している。
    // もしサブクラスでもfinalizeを実装したいなんて残念なことになったら、このクラスにFinalizer Guardianを使うこと。
    protected final void finalize() throws Throwable {
        if (refCount > 0) {
            logger.error("RESOURCE LEAK! : Connection [" + getName() + "] has been retained yet.");

            // call invalidate.
            while (refCount > 0) {
                invalidate();
            }
        }

        super.finalize();
    }

    public abstract void beginTransaction() throws DAOException;
    public abstract void commit() throws DAOException;
    public abstract void rollback() throws DAOException;
    public abstract boolean isInTransaction() throws DAOException;
}

