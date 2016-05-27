package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;

public class CliCertificateGenerator implements CertificateGenerator {

    @Override
    public String getTicket() {
        System.out.print("Please give the ticket you got from your friend: ");
        return readLine();
    }

    @Override
    public String getCaFingerprint() {
        System.out.print("Please give the fingerprint you got from your friend: ");
        return readLine();
    }

    @Override
    public String getUsername() {
        System.out.print("Please enter your name: ");
        String name = readLine();

        while (name.indexOf(',') != -1) {
            System.out.print("Comma is not allowed, try again: ");
            name = readLine();
        }
        return name;
    }

    @Override
    public String getDeviceName() {
        System.out.print("Please enter your device's name: ");
        String name = readLine();

        while (name.indexOf(',') != -1) {
            System.out.print("Comma is not allowed, try again: ");
            name = readLine();
        }
        return name;
    }

    @Override
    public int[] getUserIds() {
        InputReader scanner = new InputReader(System.in);
        String format = "Please give the Id for the %s: ";
        String[] args = {"user", "user's device"};
        int[] result = new int[2];

        for (int i = 0; i < result.length; i++) {
            System.out.print(String.format(format, args[i]));
            int id = scanner.nextInt();
            result[i] = id;
        }

        return result;
    }

    public String readLine() {
        InputReader scanner = new InputReader(System.in);
        return scanner.next();
    }
}
