package de.njsm.stocks.linux.client;


import de.njsm.stocks.linux.client.frontend.cli.CliFactory;
import de.njsm.stocks.linux.client.frontend.UIFactory;

import java.util.Scanner;

public class Main {


    public static void main (String[] args) {

        Scanner source = new Scanner(System.in);
        Configuration c = new Configuration();
        CertificateManager certManager = new CertificateManager(c);
        UIFactory f = new CliFactory();
        boolean endRequested = false;

        c.loadConfig(f);
        certManager.loadCertificates(f);

        while (! endRequested) {
            System.out.print("stocks $ ");
            String command = source.next();

            if (command.equals("quit")) {
                endRequested = true;
            }
        }
    }
}
