package de.njsm.stocks.client;


import de.njsm.stocks.client.frontend.cli.CliFactory;
import de.njsm.stocks.client.frontend.UIFactory;

public class Main {


    public static void main (String[] args) {

        Configuration c = new Configuration();
        UIFactory f = new CliFactory();

        c.loadConfig(f);

        f.getMainHandler(c).run(args);
    }
}
