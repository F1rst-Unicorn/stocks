package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
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