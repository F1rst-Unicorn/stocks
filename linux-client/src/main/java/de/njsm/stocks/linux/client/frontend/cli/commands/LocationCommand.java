package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class LocationCommand extends Command {

    public LocationCommand(Configuration c) {
        command = "loc";
        description = "Manage the locations to store food";
        this.c = c;
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            listLocations();
        } else if (commands.get(1).equals("list")) {
            listLocations();
        } else if (commands.get(1).equals("help")) {
            printHelp();
        } else if (commands.get(1).equals("add")) {
            if (commands.size() == 3){
                addLocation(commands.get(2));
            } else {
                addLocation();
            }
        } else if (commands.get(1).equals("remove")) {
            if (commands.size() == 3){
                removeLocation(commands.get(2));
            } else {
                removeLocation();
            }

        } else if (commands.get(1).equals("rename")) {
            if (commands.size() == 4) {
                renameLocation(commands.get(2), commands.get(3));
            } else {
                renameLocation();
            }
        } else {
            System.out.println("Unknown command: " + commands.get(1));
        }
    }

    @Override
    public void printHelp() {
        String help = "location command\n" +
                "\n" +
                "\thelp\t\t\tThis help screen\n" +
                "\tlist\t\t\tList the locations of the system\n" +
                "\tadd [name]\t\tAdd a location to the system\n" +
                "\tremove [name]\t\tRemove a location from the system\n";
        System.out.println(help);
    }

    public void listLocations() {
        Location[] l = c.getDatabaseManager().getLocations();
        if (l.length != 0) {
            System.out.println("Current locations: ");

            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
        } else {
            System.out.println("\tNo locations there...");
        }
    }

    public void addLocation() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Creating a new location\nName: ");
        String name = scanner.nextName();
        addLocation(name);
    }

    public void addLocation(String name) {
        Location l = new Location();
        l.name = name;

        c.getServerManager().addLocation(l);

        (new RefreshCommand(c)).refreshLocations();
    }

    public void removeLocation() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove a location\nName: ");
        String name = scanner.next();
        removeLocation(name);
    }

    public void removeLocation(String name) {
        Location[] l = c.getDatabaseManager().getLocations(name);
        int id = resolveLoc(l, name);

        for (Location loc : l) {
            if (loc.id == id){
                c.getServerManager().removeLocation(loc);
                (new RefreshCommand(c)).refreshLocations();
            }
        }
    }

    public void renameLocation() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Rename a location\nName: ");
        String name = scanner.next();
        System.out.print("New name: ");
        String newName = scanner.next();
        renameLocation(name, newName);
    }

    public void renameLocation(String name, String newName) {
        Location[] l = c.getDatabaseManager().getLocations(name);
        int id = resolveLoc(l, name);

        for (Location loc : l) {
            if (loc.id == id){
                c.getServerManager().renameLocation(loc, newName);
                (new RefreshCommand(c)).refreshLocations();
            }
        }

    }

    protected int resolveLoc(Location[] l, String name) {
        InputReader scanner = new InputReader(System.in);
        int result;

        if (l.length == 1) {
            result = l[0].id;
        } else if (l.length == 0) {
            System.out.println("No such location found: " + name);
            return -1;
        } else {
            System.out.println("Several locations found");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            System.out.print("Choose one (default " + l[0].id + "): ");
            result = scanner.nextInt(l[0].id);
        }
        return result;
    }
}
