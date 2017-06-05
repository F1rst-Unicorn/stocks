package de.njsm.stocks.backend.network;

import android.content.ContentResolver;
import de.njsm.stocks.backend.data.Update;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SyncTaskTest {

    private SyncTask uut;

    private ContentResolver resolver;

    @Before
    public void setup() throws Exception {
        resolver = mock(ContentResolver.class);
        uut = new SyncTask(resolver);
    }

    @Test
    public void unknownTableIsIgnoredMixedOrder() throws Exception {
        Update[] serverUpdates = new Update[2];
        Update[] localUpdates = new Update[1];
        serverUpdates[0] = new Update("User", new Date());
        serverUpdates[1] = new Update("Food", new Date());
        localUpdates[0] = new Update("Food", serverUpdates[0].lastUpdate);

        uut.updateOutdatedTables(serverUpdates, localUpdates);

        verifyNoMoreInteractions(resolver);
    }

    @Test
    public void unknownTableIsIgnored() throws Exception {
        Update[] serverUpdates = new Update[2];
        Update[] localUpdates = new Update[1];
        serverUpdates[0] = new Update("User", new Date());
        serverUpdates[1] = new Update("Food", new Date());
        localUpdates[0] = new Update("User", serverUpdates[0].lastUpdate);

        uut.updateOutdatedTables(serverUpdates, localUpdates);

        verifyNoMoreInteractions(resolver);
    }
}