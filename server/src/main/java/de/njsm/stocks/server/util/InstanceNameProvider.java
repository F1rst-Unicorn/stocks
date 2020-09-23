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

package de.njsm.stocks.server.util;

import org.springframework.core.io.FileSystemResource;

import javax.servlet.ServletContext;

import static de.njsm.stocks.server.util.InstanceInitialiser.INSTANCE_NAME_KEY;

public class InstanceNameProvider {

    private final ServletContext context;

    public InstanceNameProvider(ServletContext context) {
        this.context = context;
    }

    public FileSystemResource getConfigFileUrl() {
        String format = "/etc/stocks-server/%s.properties";
        return new FileSystemResource(String.format(format, getInstanceName()));
    }

    public String getCaDirectory() {
        String format = "/usr/share/stocks-server/instances/%s/CA";
        return String.format(format, getInstanceName());
    }

    public String getInstanceName() {
        return (String) context.getAttribute(INSTANCE_NAME_KEY);
    }
}
