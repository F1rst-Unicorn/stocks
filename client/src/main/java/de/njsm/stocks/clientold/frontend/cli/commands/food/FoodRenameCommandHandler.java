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

package de.njsm.stocks.clientold.frontend.cli.commands.food;

import de.njsm.stocks.clientold.business.data.Food;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import de.njsm.stocks.clientold.exceptions.InputException;
import de.njsm.stocks.clientold.exceptions.NetworkException;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.clientold.frontend.cli.commands.InputCollector;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.network.server.ServerManager;
import de.njsm.stocks.clientold.service.Refresher;

public class FoodRenameCommandHandler extends FaultyCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public FoodRenameCommandHandler(ScreenWriter writer, InputCollector inputCollector, Refresher refresher, ServerManager serverManager) {
        super(writer);
        this.command = "rename";
        this.description = "Rename a food type";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, InputException, NetworkException {
        Food food = inputCollector.determineFood(command);
        String newName = inputCollector.determineNameFromCommandOrAsk("New name: ", command);
        serverManager.renameFood(food, newName);
        refresher.refresh();
    }
}