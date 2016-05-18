package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Update;
import de.njsm.stocks.linux.client.network.server.ServerManager;

import java.util.List;

public class RefreshCommand extends Command {

    public RefreshCommand(Configuration c) {
        this.c = c;
        command = "refresh";
    }

    @Override
    public void handle(List<String> commands) {
        ServerManager sm = c.getServerManager();

        Update[] result = sm.getUpdates();

        for (Update u : result) {
            System.out.println(u.toString());
        }
    }
}
