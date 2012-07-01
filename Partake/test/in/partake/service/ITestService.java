package in.partake.service;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.fixture.PartakeTestDataProviderSet;

public interface ITestService {
    public void initialize() throws DAOException;
    public void setDefaultFixtures() throws DAOException, PartakeException;
    public PartakeTestDataProviderSet getTestDataProviderSet();
}
