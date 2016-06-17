package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.CertificateGenerator;

public class CliCertificateGenerator implements CertificateGenerator {

    protected final InputReader reader;

    public CliCertificateGenerator() {
        reader = new EnhancedInputReader(System.in);
    }

    @Override
    public String getTicket() {
        return reader.next("Please give the ticket you got from your friend: ");
    }

    @Override
    public String getCaFingerprint() {
        return reader.next("Please give the fingerprint you got from your friend: ");
    }

    @Override
    public String getUsername() {
        String name = reader.next("Please enter your name: ");

        while (!InputReader.isNameValid(name)) {
            name = reader.next("Invalid name, try again: ");
        }
        return name;
    }

    @Override
    public String getDeviceName() {
        String name = reader.next("Please enter your device's name: ");

        while (!InputReader.isNameValid(name)) {
            name = reader.next("Invalid name, try again: ");
        }
        return name;
    }

    @Override
    public int[] getUserIds() {
        String format = "Please give the Id for the %s: ";
        String[] args = {"user", "user's device"};
        int[] result = new int[2];

        for (int i = 0; i < result.length; i++) {
            result[i] = reader.nextInt(String.format(format, args[i]));
        }

        return result;
    }

}
