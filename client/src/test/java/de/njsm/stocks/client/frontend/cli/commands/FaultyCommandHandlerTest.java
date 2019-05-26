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

package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class FaultyCommandHandlerTest {

    private FaultyCommandHandler uut;

    private ScreenWriter writer;

    private static final String NETWORK_ERROR = "network_error";

    private static final String DATABASE_ERROR = "database_error";

    private static final String INPUT_ERROR = "input_error";

    private static final String SUCCESS = "success";

    @Before
    public void setup() throws Exception {
        writer = mock(ScreenWriter.class);
        uut = new FaultyCommandHandler(writer) {
            @Override
            protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
                String input = command.next();
                if (input.equals(NETWORK_ERROR)) {
                    throw new NetworkException(NETWORK_ERROR);
                }
                if (input.equals(DATABASE_ERROR)) {
                    throw new DatabaseException(DATABASE_ERROR);
                }
                if (input.equals(INPUT_ERROR)) {
                    throw new InputException(INPUT_ERROR);
                }
                writer.println(SUCCESS);
            }

            @Override
            public void handle(Command command) {
                handleWithFaultLogger(command);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void noExceptionReturnsSuccess() throws Exception {
        Command input = Command.createCommand("");

        uut.handleWithFaultLogger(input);

        verify(writer).println(SUCCESS);
    }

    @Test
    public void networkExceptionIsHandled() throws Exception {
        Command input = Command.createCommand(NETWORK_ERROR);

        uut.handleWithFaultLogger(input);

        verify(writer).println(NETWORK_ERROR);
    }

    @Test
    public void databaseExceptionIsHandled() throws Exception {
        Command input = Command.createCommand(DATABASE_ERROR);

        uut.handleWithFaultLogger(input);

        verify(writer).println("There is a problem with the local stocks copy");
    }

    @Test
    public void inputExceptionIsHandled() throws Exception {
        Command input = Command.createCommand(INPUT_ERROR);

        uut.handleWithFaultLogger(input);

        verify(writer).println(INPUT_ERROR);
    }
}