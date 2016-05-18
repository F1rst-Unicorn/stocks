package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.MainHandler;

import java.util.Scanner;

public class CliMainHandler implements MainHandler {

    @Override
    public void run() {
        boolean endRequested = false;
        Scanner source = new Scanner(System.in);

        while (! endRequested) {
            System.out.print("stocks $ ");
            String command = source.next();

            if (command.equals("quit")) {
                endRequested = true;
            }
        }
    }
}
