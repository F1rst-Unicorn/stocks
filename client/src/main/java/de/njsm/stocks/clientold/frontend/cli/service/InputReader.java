/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.frontend.cli.service;

import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.exceptions.ParseException;
import de.njsm.stocks.clientold.service.TimeProvider;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputReader {

    private PrintStream output;

    private LineReader reader;

    private TimeProvider timeProvider;

    private DateTimeFormatter format;

    public InputReader(PrintStream output, LineReader reader, TimeProvider timeProvider, DateTimeFormatter format) {
        this.output = output;
        this.reader = reader;
        this.timeProvider = timeProvider;
        this.format = format;
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
        return nextDate(prompt, null);
    }

    public LocalDate nextDate(String prompt, LocalDate defaultValue) {
        LocalDate result;

        if (defaultValue != null)
            prompt = prompt + "(" + defaultValue.format(format) + ") ";

        do {
            String input = next(prompt);
            if (input.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            try {
                result = parseDate(input);
            } catch (ParseException e) {
                result = null;
                prompt = "Invalid date. Try again: ";
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

    LocalDate parseDate(String input) throws ParseException {
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

    private static LocalDate parseRelative(String input, TimeProvider timeProvider) throws ParseException {
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
