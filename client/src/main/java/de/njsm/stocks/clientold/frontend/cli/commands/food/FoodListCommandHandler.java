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

package de.njsm.stocks.clientold.frontend.cli.commands.food;

import de.njsm.stocks.clientold.business.data.view.FoodItemView;
import de.njsm.stocks.clientold.business.data.view.FoodView;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import de.njsm.stocks.clientold.exceptions.ParseException;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.service.TimeProvider;
import de.njsm.stocks.clientold.storage.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FoodListCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(FoodListCommandHandler.class);

    private boolean existing;
    private int limit;
    private int daysLeft;
    private String location;
    private String user;
    private ValueRange range;
    private Pattern regex;

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    public FoodListCommandHandler(ScreenWriter writer,
                                  DatabaseManager dbManager,
                                  TimeProvider timeProvider) {
        super(writer);
        this.command = "list";
        this.description = "List food in stock";
        this.dbManager = dbManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            printHelp();
        } else {
            handleInternally(command);
        }
    }

    private void handleInternally(Command command) {
        try {
            parseInput(command);
            listFood();
        } catch (ParseException e) {
            writer.println("Could not parse input: " + e.getMessage());
            LOG.error("Could not parse input", e);
        }
    }

    @Override
    public void printHelp() {
        String text = "List food in the store\n" +
                "    -e                existing: Filter by food that is in store\n" +
                "    -q                quiet: Only list amounts, no dates\n" +
                "    --a range         amount: Filter amounts within range\n" +
                "    --d number        days: Filter food with <number> days left\n" +
                "    --l string        location: Filter by given location\n" +
                "    --n number        number: List at most <number> dates\n" +
                "    --r regex         regex: Filter food names by regex\n" +
                "    --u string        user: Filter by user who bought it\n";

        writer.println(text);
    }

    private void parseInput(Command command) throws ParseException {
        parseExistingFoodFlag(command);
        parseMaximumNumberOfItemsPerType(command);
        parseQuietFlag(command);
        parseLocationFilter(command);
        parseItemNumberRange(command);
        parseUserFilter(command);
        parseFoodTypeRegexFilter(command);
        parseMaximumExpirationDate(command);
    }

    private void parseExistingFoodFlag(Command command) {
        existing = command.hasArg('e');
    }

    private void parseQuietFlag(Command command) {
        limit = command.hasArg('q') ? 0 : limit;
    }

    private void parseMaximumExpirationDate(Command command) throws ParseException {
        if (command.hasArg('d')) {
            daysLeft = command.getParamInt('d');
            existing = true;
        } else {
            daysLeft = 36500;   // 100 years :P
        }
    }

    private void parseFoodTypeRegexFilter(Command command) throws ParseException {
        if (command.hasArg('r')) {
            try {
                regex = Pattern.compile(command.getParam('r'));
            } catch (PatternSyntaxException e) {
                throw new ParseException("regex is not valid");
            }
        } else {
            regex = Pattern.compile(".*");
        }
    }

    private void parseUserFilter(Command command) {
        if (command.hasArg('u')) {
            user = command.getParam('u');
        } else {
            user = "";
        }
    }

    private void parseItemNumberRange(Command command) throws ParseException {
        if (command.hasArg('a')) {
            range = command.getParamRange('a');
        } else {
            range = ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
        }
    }

    private void parseLocationFilter(Command command) {
        if (command.hasArg('l')) {
            location = command.getParam('l');
        } else {
            location = "";
        }
    }

    private void parseMaximumNumberOfItemsPerType(Command command) throws ParseException {
        if (command.hasArg('n')) {
            limit = command.getParamInt('n');
        } else {
            limit = Integer.MAX_VALUE;
        }
    }

    private void listFood() {
        try {
            List<FoodView> food = dbManager.getItems(user, location);
            String builder = renderFoodList(food);
            printRenderedList(builder);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }

    private String renderFoodList(List<FoodView> food) {
        StringBuilder outBuf = new StringBuilder();
        LocalDate today = Instant.ofEpochMilli(timeProvider.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate listUntil = today.plus(Period.ofDays(daysLeft));
        for (FoodView f : food) {
            f.getItems().removeIf((item) -> item.eatByDate.atZone(ZoneId.systemDefault()).toLocalDate().isAfter(listUntil));

            if ((!existing || (existing && !f.getItems().isEmpty())) &&
                    range.isValidValue(f.getItems().size()) &&
                    regex.matcher(f.getFood().name).find()) {

                renderHeadlineOfType(outBuf, f);
                renderItems(outBuf, f);
            }
        }
        return outBuf.toString();
    }

    private void renderHeadlineOfType(StringBuilder outBuf, FoodView f) {
        outBuf.append("    ");
        outBuf.append(f.getItems().size());
        outBuf.append("x ");
        outBuf.append(f.getFood().name);
        outBuf.append("\n");
    }

    private void renderItems(StringBuilder outBuf, FoodView f) {
        int printedItems = 0;
        if (limit > 0) {
            for (FoodItemView item : f.getItems()) {
                Instant date = item.eatByDate;
                outBuf.append("        ");
                outBuf.append(FORMAT.format(date));
                outBuf.append(" in ");
                outBuf.append(item.location);
                outBuf.append(", ");
                outBuf.append(item.user);
                outBuf.append(" @ ");
                outBuf.append(item.device);
                outBuf.append("\n");
                printedItems++;
                if (printedItems >= limit) {
                    break;
                }
            }
        }
    }

    private void printRenderedList(String list) {
        if (list.isEmpty()) {
            writer.println("No food to show...");
        } else {
            writer.println("Current food:");
            writer.println(list);
        }
    }

}
