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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class AggregatedCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AggregatedCommandHandler.class);

    private final List<AbstractCommandHandler> commandHandler;
    private String prefix;

    public AggregatedCommandHandler(ScreenWriter writer, List<AbstractCommandHandler> commands) {
        this(writer, commands, "");
    }

    public AggregatedCommandHandler(ScreenWriter writer, List<AbstractCommandHandler> commands, String prefix) {
        super(writer);
        commandHandler = commands;
        this.prefix = prefix;
    }

    public AggregatedCommandHandler(ScreenWriter writer, AbstractCommandHandler... commands) {
        super(writer);
        this.commandHandler = Arrays.asList(commands);
        this.prefix = "";
    }

    @Override
    public void handle(Command command) {
        LOG.info("Handling " + command.toString());
        String word = command.next();
        boolean commandFound = false;

        if (word.equals("") || word.equals("help")) {
            printHelp();
            commandFound = true;
        }

        for (AbstractCommandHandler c : commandHandler) {
            if (c.canHandle(word)){
                c.handle(command);
                commandFound = true;
                break;
            }
        }

        if (! commandFound){
            writer.println("Unknown command: " + word);
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void printHelp() {
        if (prefix.equals("")){
            writer.println("Possible commands:");
        } else {
            writer.println("Possible commands for " + prefix + ":");
        }

        for (AbstractCommandHandler c : commandHandler){
            writer.println("\t" + prefix + " " + c.toString());
        }
        writer.println("Type '<command> help' for specific help to that command");
    }
}
