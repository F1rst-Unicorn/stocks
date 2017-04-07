package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.CommandManager;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

public class DeviceCommandHandler extends AbstractCommandHandler {

    private final CommandManager m;

    private AbstractCommandHandler defaultHandler;

    public DeviceCommandHandler(CommandManager subcommandManager,
                                AbstractCommandHandler defaultHandler,
                                ScreenWriter writer) {
        super(writer);
        this.command = "dev";
        this.description = "Manage the devices accessing the stocks system";
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
