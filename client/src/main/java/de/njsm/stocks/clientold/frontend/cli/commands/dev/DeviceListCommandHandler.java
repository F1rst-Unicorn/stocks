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

package de.njsm.stocks.clientold.frontend.cli.commands.dev;

import de.njsm.stocks.clientold.business.data.view.UserDeviceView;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import de.njsm.stocks.clientold.exceptions.InputException;
import de.njsm.stocks.clientold.exceptions.NetworkException;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.storage.DatabaseManager;

import java.util.List;

public class DeviceListCommandHandler extends FaultyCommandHandler {

    private DatabaseManager dbManager;

    public DeviceListCommandHandler(ScreenWriter writer,
                                    DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List all the devices";
        this.dbManager = dbManager;
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, NetworkException, InputException {
        List<UserDeviceView> devices = dbManager.getDevices();
        writer.printUserDeviceViews("Current devices: ", devices);
    }
}
