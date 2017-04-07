package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.CommandManager;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

public class UserCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    private final AbstractCommandHandler defaultHandler;

    public UserCommandHandler(CommandManager subcommandManager,
                              AbstractCommandHandler defaultHandler,
                              ScreenWriter writer) {
        super(writer);
        command = "user";
        description = "Manage the users of the stocks system";
        this.defaultHandler = defaultHandler;
        this.m = subcommandManager;
        this.m.setPrefix(command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            defaultHandler.handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

}
