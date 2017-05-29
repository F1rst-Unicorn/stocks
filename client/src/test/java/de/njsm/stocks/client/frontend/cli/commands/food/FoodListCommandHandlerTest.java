package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }

    @Test
    public void listNoFoodWorks() throws Exception {
        Command input = Command.createCommand(new String[0]);
        when(dbManager.getItems(any(), any())).thenReturn(Collections.emptyList());

        uut.handle(input);

        verify(writer).println("No food to show...");
        verify(dbManager).getItems("", "");
    }

    @Test
    public void invalidRegexThrowsException() throws Exception {
        Command input = Command.createCommand("--r *");
        when(dbManager.getItems(any(), any())).thenReturn(Collections.emptyList());

        uut.handle(input);

        verify(writer).println("Could not parse input: regex is not valid");
    }
}