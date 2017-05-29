package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.ConfigGenerator;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.service.TimeProviderImpl;

public class CliConfigGenerator implements ConfigGenerator {

    private final InputReader reader;

    CliConfigGenerator(InputReader reader) {
        this.reader = reader;
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
}
