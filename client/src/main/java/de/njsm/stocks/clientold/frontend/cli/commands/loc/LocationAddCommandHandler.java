/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.clientold.frontend.cli.commands.loc;

import de.njsm.stocks.clientold.business.data.Location;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import de.njsm.stocks.clientold.exceptions.InputException;
import de.njsm.stocks.clientold.exceptions.NetworkException;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.clientold.frontend.cli.commands.InputCollector;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.network.server.ServerManager;
import de.njsm.stocks.clientold.service.Refresher;

public class LocationAddCommandHandler extends FaultyCommandHandler {

    private Refresher refresher;

    private InputCollector inputCollector;

    private ServerManager serverManager;

    public LocationAddCommandHandler(ScreenWriter writer, Refresher refresher, InputCollector inputCollector, ServerManager serverManager) {
        super(writer);
        this.command = "add";
        this.description = "Add a new food location to the system";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        Location location = inputCollector.createLocation(command);
        serverManager.addLocation(location);
        refresher.refresh();

    }
}