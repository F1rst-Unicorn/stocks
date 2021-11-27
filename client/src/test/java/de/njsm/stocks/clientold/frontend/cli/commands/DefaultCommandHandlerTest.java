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

package de.njsm.stocks.clientold.frontend.cli.commands;

import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DefaultCommandHandlerTest {

    private DefaultCommandHandler uut;

    private ScreenWriter writer;

    private AbstractCommandHandler defaultHandler;

    private AbstractCommandHandler otherHandler1;

    private AbstractCommandHandler otherHandler2;

    @Before
    public void setup() throws Exception {
        writer = mock(ScreenWriter.class);
        defaultHandler = mock(AbstractCommandHandler.class);
        otherHandler1 = mock(AbstractCommandHandler.class);
        otherHandler2 = mock(AbstractCommandHandler.class);
        uut = new DefaultCommandHandler(writer,
                defaultHandler,
                "some command",
                "description",
                otherHandler1,
                otherHandler2,
                defaultHandler);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(defaultHandler);
        verifyNoMoreInteractions(otherHandler1);
        verifyNoMoreInteractions(otherHandler2);
    }

    @Test
    public void defaultHandlerTakesEmptyCommand() throws Exception {
        Command command = Command.createCommand(new String[0]);

        uut.handle(command);

        verify(defaultHandler).handle(command);
    }

    @Test
    public void otherHandlersCanTakeOver() throws Exception {
        String rawInput = "dummy";
        Command command = Command.createCommand(rawInput);
        when(otherHandler2.canHandle(any())).thenReturn(true);

        uut.handle(command);

        verify(otherHandler1).canHandle(rawInput);
        verify(otherHandler2).canHandle(rawInput);
        verify(otherHandler2).handle(command);
    }
}
