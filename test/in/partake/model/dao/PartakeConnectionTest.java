package in.partake.model.dao;

import in.partake.app.PartakeApp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Connection 関連のテストケース。
 */
public class PartakeConnectionTest extends AbstractConnectionTestCaseBase {

    private PartakeConnection getConnection() throws DAOException {
        return PartakeApp.getDBService().getConnection();
    }

    @Test
    public void testToConnectAndRelease() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            // do nothing.
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToRetain() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            con.retain();
            try {
                // do nothing
            } finally {
                con.invalidate();
            }
        } finally {
            con.invalidate();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testToRetain2() throws DAOException {
        PartakeConnection con = getConnection();
        con.invalidate();

        con.retain(); // should throw IllegalStateException.
        Assert.fail(); // SHOULD NOT REACHED
    }

    @Test
    public void testToCommit() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            con.beginTransaction();
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testToCommitInvalidly() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            // con.beginTransaction();
            // commit without acquiring a transaction.
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToRollback() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            con.beginTransaction();
            con.rollback();
        } finally {
            con.invalidate();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testToRollbackInvalidly() throws DAOException {
        PartakeConnection con = getConnection();
        try {
            // con.beginTransaction();
            // rollback without acquiring a transaction.
            con.rollback();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToCallInvalidateWithoutCommit() throws DAOException {
        // this should success. invalidate() should call rollback when the transaction is not released.
        PartakeConnection con = getConnection();
        try {
            con.beginTransaction();
        } finally {
            con.invalidate();
        }
    }
}
