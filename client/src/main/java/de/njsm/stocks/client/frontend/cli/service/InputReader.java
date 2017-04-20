package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.ParseException;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import jline.console.history.History;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputReader {

    private PrintStream output;

    private ConsoleReader reader;

    public InputReader(ConsoleReader reader, PrintStream output) {
        this.output = output;
        try {
            this.reader = reader;

            History file = new FileHistory(new File(Configuration.STOCKS_HOME + "/history"));
            this.reader.setHistory(file);
            this.reader.setHistoryEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String next(String prompt) {
        reader.setPrompt(prompt);
        try {
            String input = reader.readLine();
            if (input == null) {
                return "\n";
            } else {
                return input;
            }
        } catch (IOException e) {
            return "";
        }
    }

    public void shutdown() {
        if (reader.getHistory() instanceof FileHistory){
            try {
                ((FileHistory) reader.getHistory()).flush();
            } catch (IOException e) {
                output.println("History not saved: " + e.getMessage());
            }
        }
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
                    output.print("That's not a number. Try again: ");
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
                output.print("That's not a number. Try again: ");
            }

        } while (result == -1);
        return result;
    }

    public Date nextDate(String prompt) {
        String input = next(prompt);
        Date result;

        do {
            try {
                result = parseDate(input);
            } catch (ParseException e) {
                input = next("Invalid date. Try again: ");
                result = null;
            }
        } while (result == null);
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
        SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
        if (input.length() > 0 && input.charAt(0) == '+') {
            result = parseRelative(input);
        } else {
            try {
                result = parser.parse(input);
            } catch (java.text.ParseException e) {
                throw new ParseException("Could not parse date", e);
            }
        }
        return result;
    }

    protected static Date parseRelative(String input) throws ParseException {
        char unit = input.charAt(input.length()-1);
        int amount;
        try {
            amount = Integer.parseInt(input.substring(1, input.length() - 1));
        } catch (NumberFormatException e) {
            throw new ParseException(input);

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
                throw new ParseException(input);
        }
        return result;
    }
}
