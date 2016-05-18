package de.njsm.stocks.linux.client.frontend.cli.commands;

import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        command = "help";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1){
            printGeneralHelp();
        }
    }

    public void printGeneralHelp() {
        String help = "Stocks linux CLI client\n" +
                "Possible commands: \n" +
                "\n";

        System.out.println(help);
    }
}
