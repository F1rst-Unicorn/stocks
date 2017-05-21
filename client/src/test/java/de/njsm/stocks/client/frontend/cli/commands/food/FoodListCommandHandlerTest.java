package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class FoodListCommandHandlerTest {

    private FoodListCommandHandler uut;

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        dbManager = mock(DatabaseManager.class);
        timeProvider = mock(TimeProvider.class);
        writer = mock(ScreenWriter.class);
        uut = new FoodListCommandHandler(writer, dbManager, timeProvider);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(dbManager);
        verifyNoMoreInteractions(timeProvider);
    }

    @Test
    public void testStuff() throws Exception {

    }
}