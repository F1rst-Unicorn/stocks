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

package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;

public class UserRemoveCommandHandler extends FaultyCommandHandler {

    private final ServerManager serverManager;

    private InputCollector inputCollector;

    private Refresher refresher;

    public UserRemoveCommandHandler(ScreenWriter writer, ServerManager serverManager, InputCollector inputCollector, Refresher refresher) {
        super(writer);
        this.command = "remove";
        this.description = "Remove a user";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        User userToRemove = inputCollector.determineUser(command);
        serverManager.removeUser(userToRemove);
        refresher.refresh();

    }
}
