package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;

public class CliCertificateGenerator implements CertificateGenerator {

    protected InputReader reader;

    public CliCertificateGenerator() {
        reader = new EnhancedInputReader(System.in);
    }

    @Override
    public String getTicket() {
        System.out.print("Please give the ticket you got from your friend: ");
        return reader.next();
    }

    @Override
    public String getCaFingerprint() {
        System.out.print("Please give the fingerprint you got from your friend: ");
        return reader.next();
    }

    @Override
    public String getUsername() {
        System.out.print("Please enter your name: ");
        String name = reader.next();

        while (name.indexOf(',') != -1) {
            System.out.print("Comma is not allowed, try again: ");
            name = reader.next();
        }
        return name;
    }

    @Override
    public String getDeviceName() {
        System.out.print("Please enter your device's name: ");
        String name = reader.next();

        while (name.indexOf(',') != -1) {
            System.out.print("Comma is not allowed, try again: ");
            name = reader.next();
        }
        return name;
    }

    @Override
    public int[] getUserIds() {
        String format = "Please give the Id for the %s: ";
        String[] args = {"user", "user's device"};
        int[] result = new int[2];

        for (int i = 0; i < result.length; i++) {
            System.out.print(String.format(format, args[i]));
            int id = reader.nextInt();
            result[i] = id;
        }

        return result;
    }

}
