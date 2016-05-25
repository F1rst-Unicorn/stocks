package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;

import java.util.List;

public abstract class Command {

    protected String command;
    protected String description;
    protected Configuration c;

    public abstract void handle(List<String> commands);

    public void printHelp() {
        System.out.println("No help page found...");
    }

    public boolean canHandle(String command) {
        return this.command.equals(command);
    }

    @Override
    public String toString() {
        int colWidth = 15;
        int count = colWidth - command.length();
        StringBuffer buf = new StringBuffer();
        buf.append(command);

        for (int i = 0; i < count; i++){
            buf.append(" ");
        }

        buf.append(description);
        return buf.toString();
    }

}
