package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    ArrayList<Command> commandHandler;

    public CommandManager(Configuration c) {
        commandHandler = new ArrayList<>();
        commandHandler.add(new RefreshCommand(c));
        commandHandler.add(new UserCommand(c));
        commandHandler.add(new LocationCommand(c));
    }

    public boolean handleCommand(List<String> commandList){
        boolean commandFound = false;

        if (commandList.get(0).equals("help")){
            printHelp();
            return true;
        }

        for (Command c : commandHandler) {
            if (c.canHandle(commandList.get(0))){
                c.handle(commandList);
                commandFound = true;
                break;
            }
        }
        return commandFound;
    }

    public void printHelp() {
        System.out.println("Stocks linux CLI client");
        System.out.println("Possible commands:");

        for (Command c : commandHandler){
            System.out.println("\t" + c.toString());
        }
        System.out.println("\n\tType '<command> help' for specific help to that command");
    }
}
