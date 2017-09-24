package de.njsm.stocks.backend.network.tasks;

import android.content.ContentResolver;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.Update;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.mockito.Mockito.mock;

public class SyncTaskTest {

    private SyncTask uut;

    @Before
    public void setup() throws Exception {
        uut = new SyncTask(new File(""),
                mock(ServerManager.class),
                mock(ContentResolver.class), null);
    }

    @Test
    public void unknownTableIsIgnoredMixedOrder() throws Exception {
        Update[] serverUpdates = new Update[2];
        Update[] localUpdates = new Update[1];
        serverUpdates[0] = new Update("User", new Date());
        serverUpdates[1] = new Update("Food", new Date());
        localUpdates[0] = new Update("Food", serverUpdates[1].lastUpdate);

        uut.updateOutdatedTables(serverUpdates, localUpdates);

    }

    @Test
    public void unknownTableIsIgnored() throws Exception {
        Update[] serverUpdates = new Update[2];
        Update[] localUpdates = new Update[1];
        serverUpdates[0] = new Update("User", new Date());
        serverUpdates[1] = new Update("Food", new Date());
        localUpdates[0] = new Update("User", serverUpdates[0].lastUpdate);

        uut.updateOutdatedTables(serverUpdates, localUpdates);

    }
}