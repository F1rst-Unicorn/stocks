package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.common.data.view.FoodView;

import de.njsm.stocks.client.exceptions.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ValueRange;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FoodListCommandHandler extends AbstractCommandHandler {

    protected boolean quiet;
    protected boolean existing;
    protected int limit;
    protected String location;
    protected long daysLeft;
    protected ValueRange range;
    protected String user;
    protected String regex;

    public FoodListCommandHandler(ScreenWriter writer, Configuration c) {
        super(writer);
        this.c = c;
        this.command = "list";
        this.description = "List food in stock";
    }

    @Override
    public void handle(Command command) {

        if (command.hasNext()) {
            String word = command.next();
            if (word.equals("help")) {
                printHelp();
            } else {
                // TODO Log
            }
            return;
        }

        try {
            quiet = command.hasArg('q');
            existing = command.hasArg('e');

            if (command.hasArg('n')) {
                limit = command.getParamInt('n');
            } else {
                limit = Integer.MAX_VALUE;
            }

            if (command.hasArg('l')) {
                location = command.getParam('l');
            } else {
                location = "";
            }

            if (command.hasArg('d')) {
                daysLeft = command.getParamInt('d');
                existing = true;
            } else {
                daysLeft = 36500;   // 100 years :P
            }

            if (command.hasArg('a')) {
                range = command.getParamRange('a');
            } else {
                range = ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
            }

            if (command.hasArg('u')) {
                user = command.getParam('u');
            } else {
                user = "";
            }

            if (command.hasArg('r')) {
                regex = command.getParam('r');
                try {
                    Pattern.compile(regex);
                } catch (PatternSyntaxException e) {
                    throw new ParseException("regex is not valid");
                }
            } else {
                regex = ".*";
            }

            listFood();
        } catch (ParseException e) {
            // TODO Log
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
                "\t--u string\t\tuser: Filter by user who bought it\n\n";

                System.out.print(text);
    }

    public void listFood() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Pattern p = Pattern.compile(regex);
            List<FoodView> food = c.getDatabaseManager().getItems(user, location);
            Date listUntil = new Date(new Date().getTime() + daysLeft * 1000L * 60L * 60L * 24L);
            int printedItems;
            StringBuffer outBuf = new StringBuffer();

            for (FoodView f : food) {
                printedItems = 0;
                f.getItems().removeIf((item) -> item.after(listUntil));

                if ((!existing || (existing && !f.getItems().isEmpty())) &&
                        range.isValidValue(f.getItems().size()) &&
                        p.matcher(f.getFood().name).find()) {

                    outBuf.append("\t" + f.getItems().size() + "x " + f.getFood().name + "\n");
                    if (!quiet) {
                        for (Date date : f.getItems()) {
                            outBuf.append("\t\t" + format.format(date) + "\n");
                            printedItems++;
                            if (printedItems >= limit) {
                                break;
                            }
                        }
                    }
                }
            }

            if (outBuf.length() != 0) {
                System.out.println("Current food: ");
                System.out.print(outBuf.toString());
            } else {
                System.out.println("No food to show...");
            }
        } catch (DatabaseException e) {
            System.out.println("Error getting items");
        }
    }

}
