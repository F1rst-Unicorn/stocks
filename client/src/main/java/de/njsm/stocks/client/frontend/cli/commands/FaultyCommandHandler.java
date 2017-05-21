package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

public abstract class FaultyCommandHandler extends AbstractCommandHandler {

    public FaultyCommandHandler(ScreenWriter writer) {
        super(writer);
    }

    @Override
    public void handle(Command command) {
        handleWithFaultLogger(command);
    }

    protected void handleWithFaultLogger(Command command) {
        try {
            handleInternally(command);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (InputException e) {
            logInputError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
        }
    }

    protected abstract void handleInternally(Command command) throws
            NetworkException,
            DatabaseException,
            InputException;

}
