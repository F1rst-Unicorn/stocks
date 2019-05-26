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

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

import java.util.List;

public class DefaultCommandHandler extends AggregatedCommandHandler {

    private final AbstractCommandHandler defaultHandler;

    public DefaultCommandHandler(ScreenWriter writer,
                                 String command,
                                 String description,
                                 AbstractCommandHandler defaultHandler,
                                 List<AbstractCommandHandler> commands) {
        super(writer, commands, command);
        this.command = command;
        this.description = description;
        this.defaultHandler = defaultHandler;
        setPrefix(command);
    }

    public DefaultCommandHandler(ScreenWriter writer,
                                 AbstractCommandHandler defaultHandler,
                                 String command,
                                 String description,
                                 AbstractCommandHandler... handlers) {
        super(writer, handlers);
        this.command = command;
        this.description = description;
        this.defaultHandler = defaultHandler;
        setPrefix(command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            super.handle(command);
        } else {
            defaultHandler.handle(command);
        }
    }
}
