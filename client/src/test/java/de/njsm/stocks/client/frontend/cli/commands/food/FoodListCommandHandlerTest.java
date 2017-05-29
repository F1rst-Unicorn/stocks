package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.MockData;
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
        verify(timeProvider, atLeastOnce()).getTime();
    }

    @Test
    public void invalidRegexThrowsException() throws Exception {
        Command input = Command.createCommand("--r *");
        when(dbManager.getItems(any(), any())).thenReturn(Collections.emptyList());

        uut.handle(input);

        verify(writer).println("Could not parse input: regex is not valid");
    }

    @Test
    public void listingFoodWithoutItems() throws Exception {
        Command input = Command.createCommand("-q --a 2");
        when(dbManager.getItems(any(), any())).thenReturn(MockData.getFoodViews());

        uut.handle(input);

        verify(dbManager).getItems("", "");
        verify(writer).println("Current food:");
        verify(writer).println("\t2x Apple juice\n");
        verify(timeProvider, atLeastOnce()).getTime();
    }

    @Test
    public void listingFoodWithItems() throws Exception {
        Command input = Command.createCommand("--a 2");
        when(dbManager.getItems(any(), any())).thenReturn(MockData.getFoodViews());

        uut.handle(input);

        verify(dbManager).getItems("", "");
        verify(writer).println("Current food:");
        verify(writer).println("\t2x Apple juice\n\t\t08.01.1970\n\t\t09.01.1970\n");
        verify(timeProvider, atLeastOnce()).getTime();
    }

    @Test
    public void listingUntilWorks() throws Exception {
        Command input = Command.createCommand("--r juice --d 7");
        when(dbManager.getItems(any(), any())).thenReturn(MockData.getFoodViews());
        when(timeProvider.getTime()).thenReturn(0L);

        uut.handle(input);

        verify(dbManager).getItems("", "");
        verify(writer).println("Current food:");
        verify(writer).println("\t1x Apple juice\n\t\t08.01.1970\n");
        verify(timeProvider, atLeastOnce()).getTime();
    }
}