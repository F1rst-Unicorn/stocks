package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;

import java.util.Arrays;
import java.util.List;

public class AggregatedCommandHandler extends AbstractCommandHandler {

    private final List<AbstractCommandHandler> commandHandler;
    private String prefix;

    public AggregatedCommandHandler(ScreenWriter writer, List<AbstractCommandHandler> commands) {
        this(writer, commands, "");
    }

    public AggregatedCommandHandler(ScreenWriter writer, List<AbstractCommandHandler> commands, String prefix) {
        super(writer);
        commandHandler = commands;
        this.prefix = prefix;
    }

    public AggregatedCommandHandler(ScreenWriter writer, AbstractCommandHandler... commands) {
        super(writer);
        this.commandHandler = Arrays.asList(commands);
        this.prefix = "";
    }

    @Override
    public void handle(Command command) {
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
            writer.println("Unknown command: " + word);
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void printHelp() {
        if (prefix.equals("")){
            writer.println("Possible commands:");
        } else {
            writer.println("Possible commands for " + prefix + ":");
        }

        for (AbstractCommandHandler c : commandHandler){
            writer.println("\t" + prefix + " " + c.toString());
        }
        writer.println("Type '<command> help' for specific help to that command");
    }
}
