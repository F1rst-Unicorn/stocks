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

import org.apache.logging.log4j.web.Log4jWebSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InstanceInitialiser implements ServletContextListener {

    public static final String INSTANCE_NAME_KEY = "de.njsm.stocks.server.instance.name";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String instanceName = sce.getServletContext().getContextPath()
                .replace('/', '.')
                .replaceAll("^\\.+", "")
                .replaceAll("\\.+$", "");
        sce.getServletContext().setAttribute(INSTANCE_NAME_KEY, instanceName);

        String log4j2ConfigFileTemplate = "file:///etc/stocks-server/log4j2-%s.xml";
        String log4j2ConfigFile = String.format(log4j2ConfigFileTemplate, instanceName);
        sce.getServletContext().setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION, log4j2ConfigFile);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
