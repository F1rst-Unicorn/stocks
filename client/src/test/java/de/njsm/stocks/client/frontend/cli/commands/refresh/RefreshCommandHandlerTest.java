/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.client.frontend.cli.commands.refresh;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RefreshCommandHandlerTest {

    private RefreshCommandHandler uut;

    private Refresher refresher;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        refresher = mock(Refresher.class);
        writer = mock(ScreenWriter.class);
        uut = new RefreshCommandHandler(writer, refresher);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(refresher);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void handleSparseRefresh() throws Exception {
        when(refresher.refresh()).thenReturn(false);

        uut.handle(Command.createCommand(new String[0]));

        verify(refresher).refresh();
        verify(writer).println("Update successful");
    }

    @Test
    public void handleFullRefresh() throws Exception {

        uut.handle(Command.createCommand("-f"));

        verify(refresher).refreshFull();
    }

    @Test
    public void sparseRefreshWithoutChangePrintsDifferentMessage() throws Exception {
        when(refresher.refresh()).thenReturn(true);

        uut.handle(Command.createCommand(new String[0]));

        verify(refresher).refresh();
        verify(writer).println("Already up to date");
    }

    @Test
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }
}