package de.njsm.stocks.android.repo;

import de.njsm.stocks.android.db.dao.UpdateDao;
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.dao.UserDeviceDao;
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.network.server.ServerClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import java.util.concurrent.Executor;

public class SynchroniserTest {

    private Synchroniser uut;

    private ServerClient serverClient;

    private UserDao userDao;

    private UserDeviceDao userDeviceDao;

    private UpdateDao updateDao;

    private Executor executor;

    @Before
    public void setup() {
        serverClient = Mockito.mock(ServerClient.class);
        userDao = Mockito.mock(UserDao.class);
        userDeviceDao = Mockito.mock(UserDeviceDao.class);
        updateDao = Mockito.mock(UpdateDao.class);
        executor = Mockito.mock(Executor.class);

        uut = new Synchroniser(serverClient, userDao, userDeviceDao, locationDao, updateDao, executor);
    }

    @Test
    public void unknownTableIsIgnoredMixedOrder() throws Exception {
        Update[] serverUpdates = new Update[] {
            new Update(0, "User", Instant.EPOCH),
            new Update(0, "Food", Instant.EPOCH),
        };
        Update[] localUpdates = new Update[] {
            new Update(0, "Food", Instant.EPOCH),
            serverUpdates[1],
        };

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }

    @Test
    public void unknownTableIsIgnored() throws Exception {
        Update[] serverUpdates = new Update[] {
                new Update(0, "Food", Instant.EPOCH),
                new Update(0, "User", Instant.EPOCH),
        };
        Update[] localUpdates = new Update[] {
                new Update(0, "Food", Instant.EPOCH),
                serverUpdates[1],
        };

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }
}