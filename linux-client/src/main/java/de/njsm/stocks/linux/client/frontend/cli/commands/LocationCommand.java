package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class LocationCommand extends Command {

    public LocationCommand(Configuration c) {
        command = "loc";
        this.c = c;
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            listLocations();
        } else if (commands.get(1).equals("list")) {
            listLocations();
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

        } else {
            System.out.println("Unknown command: " + commands.get(1));
        }
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
        InputReader scanner = new InputReader(System.in);
        Location[] l = c.getDatabaseManager().getLocations(name);
        int id;

        if (l.length == 1) {
            id = l[0].id;
        } else if (l.length == 0) {
            System.out.println("No such location found: " + name);
            return;
        } else {
            System.out.println("Several locations found");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            System.out.print("Choose one (default " + l[0].id + "): ");
            id = scanner.nextInt(l[0].id);
        }

        for (Location loc : l) {
            if (loc.id == id){
                c.getServerManager().removeLocation(loc);
            }
        }

        (new RefreshCommand(c)).refreshLocations();
    }
}
