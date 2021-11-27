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

package de.njsm.stocks.clientold.frontend.cli.commands.edit;

import de.njsm.stocks.clientold.business.data.FoodItem;
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

import java.time.Instant;

public class EditCommandHandler extends FaultyCommandHandler {

    private final InputCollector inputCollector;

    private final ServerManager serverManager;

    private Refresher refresher;

    public EditCommandHandler(ServerManager serverManager,
                              InputCollector inputCollector,
                              ScreenWriter writer,
                              Refresher refresher) {
        super(writer);
        command = "edit";
        description = "Edit a food item to change location or due date";
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
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
    public void printHelp() {
        String text = "Edit a food item to change location or due date\n" +
                "\t--f string\t\t\tfood: The food type to move\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n" +
                "\t--d date  \t\t\tdate: Eat before this date\n";

        writer.println(text);
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        FoodItem item = inputCollector.determineItem(command);
        Location location = inputCollector.determineDestinationLocation(command);
        Instant newEatByDate = inputCollector.determineDate(command, item.eatByDate);
        serverManager.edit(item, newEatByDate, location.id);
        refresher.refresh();
    }
}
