package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.frontend.MainHandler;
import de.njsm.stocks.linux.client.frontend.cli.commands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CliMainHandler implements MainHandler {

    protected CommandManager m;
    protected Configuration c;

    public CliMainHandler(Configuration c) {
        this.c = c;

        ArrayList<Command> commandHandler = new ArrayList<>();
        commandHandler.add(new RefreshCommand(c));
        commandHandler.add(new UserCommand(c));
        commandHandler.add(new LocationCommand(c));
        commandHandler.add(new DeviceCommand(c));

        m = new CommandManager(commandHandler);
    }

    @Override
    public void run() {
        boolean endRequested = false;
        InputReader source = new InputReader(System.in);

        while (! endRequested) {
            System.out.print("stocks $ ");
            String command = source.next();

            if (command.equals("quit")) {
                endRequested = true;
            } else if (command.equals("")) {
                // usefully do nothing
            } else {
                forwardCommand(command);
            }
        }
    }

    public void forwardCommand(String command) {
        List<String> commandList = parseCommand(command);
        m.handleCommand(commandList);
    }

    public List<String> parseCommand(String command) {
        String[] commands = command.split(" ");
        LinkedList<String> result = new LinkedList<>();
        for (String s : commands) {
            result.add(s);
        }
        return result;
    }
}
