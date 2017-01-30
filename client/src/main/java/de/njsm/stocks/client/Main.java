package de.njsm.stocks.client;


import de.njsm.stocks.client.frontend.cli.CliFactory;
import de.njsm.stocks.client.frontend.UIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);


    public static void main (String[] args) {

        Configuration c = new Configuration();
        UIFactory f = new CliFactory();

        c.loadConfig(f);

        f.getMainHandler(c).run(args);
    }
}
