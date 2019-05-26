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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractCommandHandler.class);

    protected final ScreenWriter writer;

    protected String command;

    protected String description;

    public AbstractCommandHandler(ScreenWriter writer) {
        this.writer = writer;
    }

    public abstract void handle(Command command);

    public void printHelp() {
        writer.println("No help page found...");
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
        writer.println(e.getMessage());
        LOG.error("Network action failed", e);
    }

    protected void logDatabaseError(DatabaseException e) {
        writer.println("There is a problem with the local stocks copy");
        LOG.error("Database action failed", e);
    }

    protected void logInputError(InputException e) {
        writer.println(e.getMessage());
        LOG.warn("Problem with the input", e);
    }
}
