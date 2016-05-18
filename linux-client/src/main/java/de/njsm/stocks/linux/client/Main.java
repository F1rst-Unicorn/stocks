package de.njsm.stocks.linux.client;


import de.njsm.stocks.linux.client.frontend.cli.CliFactory;
import de.njsm.stocks.linux.client.frontend.UIFactory;

import java.util.Scanner;

public class Main {


    public static void main (String[] args) {

        Configuration c = new Configuration();
        UIFactory f = new CliFactory();

        c.loadConfig(f);

        f.getMainHandler(c).run();
    }
}
