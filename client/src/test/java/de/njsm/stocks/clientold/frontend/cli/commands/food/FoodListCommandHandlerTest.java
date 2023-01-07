/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.frontend.cli.commands.food;

import de.njsm.stocks.clientold.MockData;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.service.TimeProvider;
import de.njsm.stocks.clientold.storage.DatabaseManager;
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
        verify(writer).println("    2x Apple juice\n");
        verify(timeProvider, atLeastOnce()).getTime();
    }

    @Test
    public void listingFoodWithItems() throws Exception {
        Command input = Command.createCommand("--a 2");
        when(dbManager.getItems(any(), any())).thenReturn(MockData.getFoodViews());

        uut.handle(input);

        verify(dbManager).getItems("", "");
        verify(writer).println("Current food:");
        verify(writer).println("    2x Apple juice\n" +
                "        08.01.1970 in Cupboard, Juliette @ Mobile\n" +
                "        09.01.1970 in Basement, Juliette @ Mobile\n");
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
        verify(writer).println("    1x Apple juice\n        08.01.1970 in Cupboard, Juliette @ Mobile\n");
        verify(timeProvider, atLeastOnce()).getTime();
    }
}
