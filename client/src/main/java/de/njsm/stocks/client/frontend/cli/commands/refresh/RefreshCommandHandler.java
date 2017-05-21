package de.njsm.stocks.client.frontend.cli.commands.refresh;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.Refresher;

public class RefreshCommandHandler extends FaultyCommandHandler {

    private Refresher refresher;

    public RefreshCommandHandler(ScreenWriter writer, Refresher refresher) {
        super(writer);
        command = "refresh";
        description = "Refresh the stocks system from the server";
        this.refresher = refresher;
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            printHelp();
        } else {
            handleWithFaultLogger(command);
        }
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, NetworkException {
        boolean fullRefresh = command.hasArg('f');
        if (fullRefresh) {
            refresher.refreshFull();
        } else {
            refreshSparse();
        }
    }

    private void refreshSparse() throws DatabaseException, NetworkException {
        boolean upToDate = refresher.refresh();
        if (upToDate) {
            writer.println("Already up to date");
        } else {
            writer.println("Update successful");
        }
    }

    @Override
    public void printHelp() {
        String help = "Get the latest updates from the server\n" +
                "\t-f\t\t\tForce update from server";
        writer.println(help);
    }
}
