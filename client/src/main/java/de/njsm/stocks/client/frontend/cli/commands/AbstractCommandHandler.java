package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractCommandHandler.class);

    protected final ScreenWriter writer;

    protected String command;

    protected String description;

    protected Configuration c;

    public AbstractCommandHandler(ScreenWriter writer) {
        this.writer = writer;
    }

    public abstract void handle(Command command);

    public void printHelp() {
        System.out.println("No help page found...");
    }

    public boolean canHandle(String command) {
        return this.command.equals(command);
    }

    @Override
    public String toString() {
        int colWidth = 15;
        int count = colWidth - command.length();
        StringBuilder buf = new StringBuilder();
        buf.append(command);

        for (int i = 0; i < count; i++){
            buf.append(" ");
        }

        buf.append(description);
        return buf.toString();
    }

    protected void logNetworkError(NetworkException e) {
        writer.println("There is a problem with the server connection");
        LOG.error("Network action failed", e);
    }

    protected void logDatabaseError(DatabaseException e) {
        writer.println("There is a problem with the local stocks copy");
        LOG.error("Database action failed", e);
    }

    protected void logInputError(InputException e) {
        writer.println(e.getMessage());
        LOG.error("Problem with the input", e);
    }
}
