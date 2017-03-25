package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;

import java.util.List;

public class CommandManager {

    protected final List<AbstractCommandHandler> commandHandler;
    protected final String prefix;

    public CommandManager(List<AbstractCommandHandler> commands) {
        this(commands, "");
    }

    public CommandManager(List<AbstractCommandHandler> commands, String prefix) {
        commandHandler = commands;
        this.prefix = prefix;
    }

    public void handleCommand(Command command) {
        String word = command.next();
        boolean commandFound = false;

        if (word.equals("") || word.equals("help")) {
            printHelp();
            commandFound = true;
        }

        for (AbstractCommandHandler c : commandHandler) {
            if (c.canHandle(word)){
                c.handle(command);
                commandFound = true;
                break;
            }
        }

        if (! commandFound){
            System.out.println("Unknown command: " + word);
        }
    }

    public void printHelp() {
        if (prefix.equals("")){
            System.out.println("Possible commands:");
        } else {
            System.out.println("Possible commands for " + prefix + ":");
        }

        for (AbstractCommandHandler c : commandHandler){
            System.out.println("\t" + prefix + " " + c.toString());
        }
        System.out.println("Type '<command> help' for specific help to that command");
    }
}
