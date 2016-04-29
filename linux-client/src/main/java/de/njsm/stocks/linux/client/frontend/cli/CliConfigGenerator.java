package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.ConfigGenerator;

import java.util.Scanner;

public class CliConfigGenerator implements ConfigGenerator {

    @Override
    public void startUp() {
        System.out.println("Welcome to stocks.\nThis is the first time you use the app, please follow\n" +
                "this setup guide first. ");
    }

    @Override
    public String getServerName() {
        System.out.print("Please give the URL of the server (localhost): ");
        String serverName = readLine();
        return serverName.equals("") ? "localhost" : serverName;
    }

    @Override
    public int[] getPorts() {
        String format = "Please give the %s port of the server (%d): ";
        String[] ports = {"CA", "ticket server", "main server"};
        int[] defaults = {10910, 10911, 10912};
        int[] result = new int[3];
        int port = -1;
        boolean success;

        for (int i = 0; i < result.length; i++) {
            System.out.print(String.format(format, ports[i], defaults[i]));
            success = false;
            while (!success) {
                try {
                    String value = readLine();
                    port = value.equals("") ? defaults[i] : Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number, expecting only digits, range 1-65535");
                    continue;
                }
                success = true;
            }
            result[i] = port;
        }


        return result;
    }

    @Override
    public void shutDown() {
        System.out.println("Configuration successful");
    }

    public String readLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }
}
