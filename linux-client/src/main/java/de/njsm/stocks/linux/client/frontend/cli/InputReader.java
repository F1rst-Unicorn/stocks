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

    public String next() {
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[32];
        int bytesRead;

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

    public String nextName() {
        String result;

        result = next();

        while (! isNameValid(result)) {
            System.out.print("Name may not contain '$' or '='. Try again: ");
            result = next();
        }
        return result;
    }

    public int nextInt(int defaultResult) {
        String input;
        int result;
        do {
            input = next();
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

    public int nextInt() {
        String input;
        int result;
        do {
            input = next();
            try {
                result = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                result = -1;
                System.out.print("That's not a number. Try again: ");
            }

        } while (result == -1);
        return result;
    }

    public Date nextDate() {
        SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yy");
        String input = next();
        Date result = null;

        while (result == null) {
            try {
                if (input.length() > 0 && input.charAt(0) == '+') {
                    result = parseRelative(input);
                } else {
                    result = parser.parse(input);
                }

            } catch (ParseException e) {
                System.out.print("Invalid date. Try again: ");
                input = next();
                result = null;
            }
        }
        return result;
    }

    public boolean getYesNo() {
        String input = next();
        return input.equals("y");
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

    protected Date parseRelative(String input) throws ParseException {
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
