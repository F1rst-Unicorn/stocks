package de.njsm.stocks.client;


import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.config.PropertiesFileHandler;
import de.njsm.stocks.client.config.PropertiesFileHandlerImpl;
import de.njsm.stocks.client.exceptions.PrintableException;
import de.njsm.stocks.client.frontend.cli.CliFactory;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.init.InitManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);


    public static void main (String[] args) {

        try {
            LOG.info("Starting up");
            UIFactory f = new CliFactory();
            PropertiesFileHandler fileHandler = new PropertiesFileHandlerImpl();

            InitManager im = new InitManager(f, fileHandler);
            Configuration c = new Configuration(fileHandler);

            im.initialise();

            c.loadConfig();

            f.getMainHandler(c).run(args);
        } catch (PrintableException e) {
            LOG.error("", e);
            System.err.println(e.getMessage());
            System.err.println("For details consider the log file at " +
                    "~/.stocks/stocks.log");
            System.exit(1);
        }
    }
}
