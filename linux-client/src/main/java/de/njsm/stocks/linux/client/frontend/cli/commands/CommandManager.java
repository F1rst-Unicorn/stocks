package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    protected List<Command> commandHandler;
    protected String prefix;

    public CommandManager(List<Command> commands) {
        this(commands, "");
    }

    public CommandManager(List<Command> commands, String prefix) {
        commandHandler = commands;
        this.prefix = prefix;
    }

    public void handleCommand(List<String> commandList){
        boolean commandFound = false;

        if (commandList.isEmpty() || commandList.get(0).equals("help")){
            printHelp();
        }

        for (Command c : commandHandler) {
            if (c.canHandle(commandList.get(0))){
                commandList.remove(0);
                if (!commandList.isEmpty() && commandList.get(0).equals("help")) {
                    c.printHelp();
                } else {
                    c.handle(commandList);
                }
                commandFound = true;
                break;
            }
        }

        if (! commandFound){
            System.out.println("Unknown command: " + commandList.get(0));
        }
    }

    public void printHelp() {
        if (prefix.equals("")){
            System.out.println("Possible commands for :");
        } else {
            System.out.println("Possible commands for " + prefix + ":");
        }

        for (Command c : commandHandler){
            System.out.println("\t" + prefix + " " + c.toString());
        }
        System.out.println("Type '<command> help' for specific help to that command");
    }
}
