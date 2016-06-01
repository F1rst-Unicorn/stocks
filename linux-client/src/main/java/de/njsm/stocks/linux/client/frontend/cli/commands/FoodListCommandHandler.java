package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.view.FoodView;
import sun.security.krb5.internal.PAEncTSEnc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FoodListCommandHandler extends CommandHandler {

    protected boolean quiet;
    protected boolean existing;
    protected int limit;
    protected String location;
    protected int daysLeft;

    public FoodListCommandHandler(Configuration c) {
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
                c.getLog().severe("Unknown command " + word);
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

            listFood();
        } catch (ParseException e) {
            c.getLog().severe(e.getMessage());
        }
    }

    @Override
    public void handle(List<String> commands) {
        listFood();
    }

    @Override
    public void printHelp() {
        String text = "List food in the store\n" +
                "\t-q\t\t\t\t\tquiet: Only list amounts, no dates\n" +
                "\t-e\t\t\t\t\texisting: Only list food that is in store\n" +
                "\t--n number\t\t\tnumber: List at most <number> dates\n" +
                "\t--l string\t\t\tlocation: Filter by given location\n" +
                "\t--d number\t\t\tdays: Only list food with <number> days left\n\n";

        System.out.print(text);
    }

    public void listFood() {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        FoodView[] food = queryDatabase(location);
        Date listUntil = new Date(new Date().getTime() + daysLeft * 1000 * 60 * 60 * 24);
        int printedItems;

        if (food.length == 0) {
            System.out.println("\tNo food here...");
            return;
        }

        System.out.println("Current food: ");
        for (FoodView f : food) {
            printedItems = 0;
            f.getItems().removeIf((item) -> item.eatByDate.after(listUntil));

            if (!existing ||
                    (existing && !f.getItems().isEmpty())) {

                System.out.println("\t" + f.getItems().size() + "x " + f.getFood().name);
                if (!quiet) {
                    for (FoodItem i : f.getItems()) {
                        System.out.println("\t\t" + format.format(i.eatByDate));
                        printedItems++;
                        if (printedItems >= limit) {
                            break;
                        }
                    }
                }
            }
        }

    }

    protected FoodView[] queryDatabase(String location) {
        if (location.equals("")) {
            return c.getDatabaseManager().getItems();
        } else {
            return c.getDatabaseManager().getItems(location);
        }
    }

}
