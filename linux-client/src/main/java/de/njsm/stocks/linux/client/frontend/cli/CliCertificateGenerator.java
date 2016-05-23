package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;

public class CliCertificateGenerator implements CertificateGenerator {

    @Override
    public String getTicket() {
        System.out.print("Please give the ticket you got from your friend: ");
        String ticket = readLine();
        return ticket;
    }

    @Override
    public String getCaFingerprint() {
        System.out.print("Please give the fingerprint you got from your friend: ");
        String fingerprint = readLine();
        return fingerprint;
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
    public String getDevicename() {
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
        String format = "Please give the Id for the %s: ";
        String[] args = {"user", "user's device"};
        int[] result = new int[2];
        int id = -1;
        boolean success;

        for (int i = 0; i < result.length; i++) {
            System.out.print(String.format(format, args[i]));
            success = false;
            while (!success) {
                try {
                    String value = readLine();
                    id = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number: " + e.getMessage());
                    continue;
                }
                success = true;
            }
            result[i] = id;
        }

        return result;
    }

    public String readLine() {
        InputReader scanner = new InputReader(System.in);
        return scanner.next();
    }
}
