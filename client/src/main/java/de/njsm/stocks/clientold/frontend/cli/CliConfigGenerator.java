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

import de.njsm.stocks.clientold.frontend.ConfigGenerator;
import de.njsm.stocks.clientold.frontend.cli.service.InputReader;

public class CliConfigGenerator implements ConfigGenerator {

    private final InputReader reader;

    CliConfigGenerator(InputReader reader) {
        this.reader = reader;
    }

    @Override
    public String getServerName() {
        String serverName = reader.next("Please give the hostname of the server (localhost): ");
        return serverName.equals("") ? "localhost" : serverName;
    }

    @Override
    public int[] getPorts() {
        String format = "Please give the %s port of the server";
        String[] ports = {"CA", "ticket server", "main server"};
        int[] defaults = {10910, 10911, 10912};
        int[] result = new int[3];

        for (int i = 0; i < result.length; i++) {
            result[i] = reader.nextInt(String.format(format, ports[i]), defaults[i]);
        }
        return result;
    }
}
