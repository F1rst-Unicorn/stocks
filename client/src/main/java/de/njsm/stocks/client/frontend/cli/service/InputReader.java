package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.service.TimeProvider;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class InputReader {

    private PrintStream output;

    private LineReader reader;

    private TimeProvider timeProvider;

    public InputReader(LineReader reader, PrintStream output, TimeProvider timeProvider) {
        this.output = output;
        this.timeProvider = timeProvider;
        this.reader = reader;
    }

    public String next(String prompt) {
        try {
            String input = reader.readLine(prompt);
            if (input == null) {
                return "\n";
            } else {
                return input;
            }
        } catch (EndOfFileException e) {
            return "quit";
        }
    }

    public void shutdown() {
        try {
            reader.getHistory().save();
        } catch (IOException e) {
            output.println("History not saved: " + e.getMessage());
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

    public LocalDate nextDate(String prompt) {
        String input = next(prompt);
        LocalDate result;

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

    public LocalDate parseDate(String input) throws ParseException {
        return parseDate(input, timeProvider);
    }

    public static LocalDate parseDate(String input, TimeProvider timeProvider) throws ParseException {
        LocalDate result;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (input.length() > 0 && input.charAt(0) == '+') {
            result = parseRelative(input, timeProvider);
        } else {
            try {
                result = LocalDate.from(parser.parse(input));
            } catch (DateTimeParseException e) {
                throw new ParseException("Could not parse date", e);
            }
        }
        return result;
    }

    protected static LocalDate parseRelative(String input, TimeProvider timeProvider) throws ParseException {
        char unit = input.charAt(input.length()-1);
        int amount;
        try {
            amount = Integer.parseInt(input.substring(1, input.length() - 1));
        } catch (NumberFormatException e) {
            throw new ParseException(input);

        }

        LocalDate today = Instant.ofEpochMilli(timeProvider.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        Period periodFromNow;
        switch (unit) {
            case 'd':
                periodFromNow = Period.ofDays(amount);
                break;
            case 'm':
                periodFromNow = Period.ofDays(amount * 30);
                break;
            default:
                throw new ParseException(input);
        }
        return today.plus(periodFromNow);
    }

    public static LineReader buildReader() throws IOException {
        return LineReaderBuilder.builder()
                .history(new DefaultHistory())
                .terminal(TerminalBuilder.terminal())
                .variable(LineReader.HISTORY_FILE, new File(Configuration.STOCKS_HOME + "/history"))
                .build();

    }
}
