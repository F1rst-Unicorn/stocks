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

package de.njsm.stocks.clientold.frontend.cli;

import de.njsm.stocks.clientold.exceptions.ParseException;
import de.njsm.stocks.clientold.frontend.MainHandler;
import de.njsm.stocks.clientold.frontend.cli.commands.AggregatedCommandHandler;
import de.njsm.stocks.clientold.frontend.cli.service.InputReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CliMainHandler implements MainHandler {

    private static final Logger LOG = LogManager.getLogger(CliMainHandler.class);

    private final AggregatedCommandHandler m;

    private InputReader reader;

    CliMainHandler(AggregatedCommandHandler m, InputReader reader) {
        this.m = m;
        this.reader = reader;
    }

    @Override
    public void run(String[] args) {
        boolean endRequested = false;
        Command command;

        if (args.length > 0) {
            try {
                command = Command.createCommand(args);
                m.handle(command);
            } catch (ParseException e) {
                LOG.error("Could not parse command", e);
            }
        } else {
            while (!endRequested) {
                String input = reader.next("stocks $ ");

                switch (input) {
                    case "quit":
                        endRequested = true;
                        break;
                    case "":
                    case "\n":
                        break;
                    default:
                        try {
                            command = Command.createCommand(input);
                            m.handle(command);
                        } catch (ParseException e) {
                            LOG.error("Could not parse command", e);
                        }
                }
            }
        }
        reader.shutdown();
    }
}
