package de.njsm.stocks.client.frontend.cli.commands.refresh;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

public class RefreshCommandHandler extends AbstractCommandHandler {

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            printHelp();
        } else {
            handleInternally(command.hasArg('f'));
        }
    }

    private Refresher refresher;

    public RefreshCommandHandler(ScreenWriter writer, Refresher refresher) {
        super(writer);
        command = "refresh";
        description = "Refresh the stocks system from the server";
        this.refresher = refresher;
    }

    private void handleInternally(boolean fullRefresh) {
        try {
            if (fullRefresh) {
                refresher.refreshFull();
            } else {
                refreshSparse();
            }
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
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
