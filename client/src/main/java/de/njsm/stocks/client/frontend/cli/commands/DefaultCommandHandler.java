package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

import java.util.List;

public class DefaultCommandHandler extends AggregatedCommandHandler {

    private final AbstractCommandHandler defaultHandler;

    public DefaultCommandHandler(ScreenWriter writer,
                                 String command,
                                 String description,
                                 AbstractCommandHandler defaultHandler,
                                 List<AbstractCommandHandler> commands) {
        super(writer, commands, command);
        this.command = command;
        this.description = description;
        this.defaultHandler = defaultHandler;
        setPrefix(command);
    }

    public DefaultCommandHandler(ScreenWriter writer,
                                 AbstractCommandHandler defaultHandler,
                                 String command,
                                 String description,
                                 AbstractCommandHandler... handlers) {
        super(writer, handlers);
        this.command = command;
        this.description = description;
        this.defaultHandler = defaultHandler;
        setPrefix(command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            super.handle(command);
        } else {
            defaultHandler.handle(command);
        }
    }

    @Override
    public void printHelp() {
        super.printHelp();
    }

}
