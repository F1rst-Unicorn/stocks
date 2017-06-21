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

        verify(writer).println("There is a problem with the server connection");
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