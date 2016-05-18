package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;

import java.util.List;

public abstract class Command {

    protected String command;
    protected Configuration c;

    public abstract void handle(List<String> commands);

    public boolean canHandle(String command) {
        return this.command.equals(command);
    }
}
