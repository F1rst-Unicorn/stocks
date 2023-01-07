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

package de.njsm.stocks.clientold;


import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.exceptions.PrintableException;
import de.njsm.stocks.clientold.frontend.MainHandler;
import de.njsm.stocks.clientold.init.InitManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public static void main (String[] args) {
        int exitCode = 0;
        LOG.info("Starting up");

        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
            InitManager im = context.getBean(InitManager.class);
            Configuration c = context.getBean(Configuration.class);

            im.initialise();
            c.initialise();

            MainHandler runner = context.getBean(MainHandler.class);
            runner.run(args);
        } catch (PrintableException e) {
            exitCode = handleError(e);
        } catch (Exception e) {
            exitCode = handleUnexpectedError(e);
        } finally {
            LOG.info("Shutting down");
            threadPool.shutdown();
        }
        System.exit(exitCode);
    }

    private static int handleUnexpectedError(Exception e) {
        LOG.error("", e);
        System.out.println("An unexpected error has occured!");
        System.out.println("Please consider sending the log file at ");
        System.out.println("~/.stocks/stocks.log to the developers");
        return 2;
    }

    private static int handleError(PrintableException e) {
        LOG.error("", e);
        System.out.println(e.getMessage());
        System.out.println("For details consider the log file at " +
                "~/.stocks/stocks.log");
        return 1;
    }
}
