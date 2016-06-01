package de.njsm.stocks.linux.client.frontend.cli;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputReader {

    protected final InputStream in;



    public InputReader(InputStream input) {
        in = input;
    }

    public String next(String prompt) {
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[32];
        int bytesRead;

        System.out.print(prompt);
        try {
            do {
                bytesRead = in.read(buffer);
                boolean newLineFound = false;
                int i;

                for (i = 0; i < bytesRead; i++) {
                    if (buffer[i] == (byte) '\n'){
                        newLineFound = true;
                        break;
                    }
                }

                if (newLineFound) {
                    result.append(new String(buffer, 0, i));
                    break;
                } else {
                    result.append(new String(buffer));
                }

            } while (bytesRead != -1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
        return result.toString();
    }

    public String nextName(String prompt) {
        String result;

        result = next(prompt);

        while (! isNameValid(result)) {
            result = next("Name may not contain '$' or '='. Try again: ");
        }
        return result;
    }

    public int nextInt(String prompt, int defaultResult) {
        String input;
        int result;
        do {
            input = next(prompt + " (" + defaultResult + "): ");
            if (input.equals("")){
                result = defaultResult;
            } else {
                try {
                    result = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    result = Integer.MIN_VALUE;
                    System.out.print("That's not a number. Try again: ");
                }
            }
        } while (result == Integer.MIN_VALUE);
        return result;
    }

    public int nextInt(String prompt) {
        String input;
        int result;
        do {
            input = next(prompt);
            try {
                result = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                result = -1;
                System.out.print("That's not a number. Try again: ");
            }

        } while (result == -1);
        return result;
    }

    public Date nextDate(String prompt) {
        String input = next(prompt);
        Date result = null;

        while (result == null) {
            try {
                result = parseDate(input);
            } catch (ParseException e) {
                input = next("Invalid date. Try again: ");
                result = null;
            }
        }
        return result;
    }

    public boolean getYesNo() {
        String input = next(" (y/N): ");
        return input.equals("y");
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

    public static Date parseDate(String input) throws ParseException {
        Date result;
        SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yy");
        if (input.length() > 0 && input.charAt(0) == '+') {
            result = parseRelative(input);
        } else {
            result = parser.parse(input);
        }
        return result;
    }

    protected static Date parseRelative(String input) throws ParseException {
        char unit = input.charAt(input.length()-1);
        int amount;
        try {
            amount = Integer.parseInt(input.substring(1, input.length() - 1));
        } catch (NumberFormatException e) {
            throw new ParseException(input, 1);
        }

        Date result;
        switch (unit) {
            case 'd':
                result = new Date(new Date().getTime() + amount * 1000L * 60L * 60L * 24L);
                break;
            case 'm':
                result = new Date(new Date().getTime() + amount * 1000L * 60L * 60L * 24L * 30L);
                break;
            default:
                throw new ParseException(input, input.length()-1);
        }
        return result;
    }

    public void shutdown() {

    }


}
