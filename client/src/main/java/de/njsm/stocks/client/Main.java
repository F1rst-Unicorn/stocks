package de.njsm.stocks.client;


import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.config.PropertiesFileHandler;
import de.njsm.stocks.client.config.PropertiesFileHandlerImpl;
import de.njsm.stocks.client.exceptions.PrintableException;
import de.njsm.stocks.client.frontend.cli.CliFactory;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.init.InitManager;

public class Main {


    public static void main (String[] args) {

        try {
            UIFactory f = new CliFactory();
            PropertiesFileHandler fileHandler = new PropertiesFileHandlerImpl();

            InitManager im = new InitManager(f, fileHandler);
            Configuration c = new Configuration(fileHandler);

            im.initialise();

            c.loadConfig();

            f.getMainHandler(c).run(args);
        } catch (PrintableException e) {
            System.err.println(e.getMessage());
            System.err.println("For details consider the log file");
            System.exit(1);
        }
    }
}
