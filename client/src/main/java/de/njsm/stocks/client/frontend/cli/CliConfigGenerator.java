package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.ConfigGenerator;

public class CliConfigGenerator implements ConfigGenerator {

    protected final InputReader reader;

    public CliConfigGenerator() {
        reader = new EnhancedInputReader(System.in);
    }

    @Override
    public void startUp() {
        System.out.println("Welcome to stocks.\nThis is the first time you use the app, please follow\n" +
                "this setup guide first. ");
    }

    @Override
    public String getServerName() {
        String serverName = reader.next("Please give the URL of the server (localhost): ");
        return serverName.equals("") ? "localhost" : serverName;
    }

    @Override
    public int[] getPorts() {
        String format = "Please give the %s port of the server";
        String[] ports = {"CA", "ticket server", "main server"};
        int[] defaults = {10910, 10911, 10912};
        int[] result = new int[3];

        for (int i = 0; i < result.length; i++) {
            result[i] = reader.nextInt(String.format(format, ports[i]), defaults[i]);
        }
        return result;
    }

    @Override
    public void shutDown() {
        System.out.println("Configuration successful");
    }

}
