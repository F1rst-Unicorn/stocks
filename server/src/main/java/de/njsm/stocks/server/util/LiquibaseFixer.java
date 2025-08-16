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

package de.njsm.stocks.server.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class LiquibaseFixer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // https://github.com/spring-projects/spring-boot/issues/1792
        System.setProperty("liquibase.scan.packages", "liquibase.change,liquibase.database," +
                "liquibase.parser,liquibase.precondition,liquibase.datatype," +
                "liquibase.serializer,liquibase.sqlgenerator,liquibase.executor," +
                "liquibase.snapshot,liquibase.logging,liquibase.diff," +
                "liquibase.structure,liquibase.structurecompare,liquibase.lockservice," +
                "liquibase.ext,liquibase.changelog");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
