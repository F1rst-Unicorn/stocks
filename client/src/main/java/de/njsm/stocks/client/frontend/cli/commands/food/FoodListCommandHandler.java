package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.view.FoodView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.temporal.ValueRange;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FoodListCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractCommandHandler.class);

    private boolean existing;
    private int limit;
    private String location;
    private long daysLeft;
    private ValueRange range;
    private String user;
    private Pattern regex;

    private DatabaseManager dbManager;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public FoodListCommandHandler(ScreenWriter writer, DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List food in stock";
        this.dbManager = dbManager;
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
            printHelp();
        }
    }

    @Override
    public void printHelp() {
        String text = "List food in the store\n" +
                "\t-e\t\t\texisting: Filter by food that is in store\n" +
                "\t-q\t\t\tquiet: Only list amounts, no dates\n" +
                "\t--a range \t\tamount: Filter amounts within range\n" +
                "\t--d number\t\tdays: Filter food with <number> days left\n" +
                "\t--l string\t\tlocation: Filter by given location\n" +
                "\t--n number\t\tnumber: List at most <number> dates\n" +
                "\t--r regex \t\tregex: Filter food names by regex\n" +
                "\t--u string\t\tuser: Filter by user who bought it\n";

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
            StringBuilder builder = renderFoodList(food);
            printRenderedList(builder);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }

    private StringBuilder renderFoodList(List<FoodView> food) {
        StringBuilder outBuf = new StringBuilder();
        for (FoodView f : food) {
            Date listUntil = new Date(new Date().getTime() + daysLeft * 1000L * 60L * 60L * 24L);
            f.getItems().removeIf((item) -> item.after(listUntil));

            if ((!existing || (existing && !f.getItems().isEmpty())) &&
                    range.isValidValue(f.getItems().size()) &&
                    regex.matcher(f.getFood().name).find()) {

                renderHeadlineOfType(outBuf, f);
                renderItems(outBuf, f);
            }
        }
        return outBuf;
    }

    private void renderHeadlineOfType(StringBuilder outBuf, FoodView f) {
        outBuf.append("\t");
        outBuf.append(f.getItems().size());
        outBuf.append("x ");
        outBuf.append(f.getFood().name);
        outBuf.append("\n");
    }

    private void renderItems(StringBuilder outBuf, FoodView f) {
        int printedItems = 0;
        if (limit > 0) {
            for (Date date : f.getItems()) {
                outBuf.append("\t\t" + FORMAT.format(date) + "\n");
                printedItems++;
                if (printedItems >= limit) {
                    break;
                }
            }
        }
    }

    private void printRenderedList(StringBuilder outBuf) {
        if (outBuf.length() != 0) {
            writer.println("Current food: ");
            writer.println(outBuf.toString());
        } else {
            writer.println("No food to show...");
        }
    }

}
