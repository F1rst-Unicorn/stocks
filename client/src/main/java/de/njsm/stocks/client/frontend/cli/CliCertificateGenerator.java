package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.CertificateGenerator;

public class CliCertificateGenerator implements CertificateGenerator {

    private final InputReader reader;

    CliCertificateGenerator() {
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
    public int getUserId() {
        return getId("User");
    }

    @Override
    public int getDeviceId() {
        return getId("device");
    }

    private int getId(String key) {
        String prompt = String.format("Please give the Id for the %s: ", key);
        return reader.nextInt(prompt);
    }
}
