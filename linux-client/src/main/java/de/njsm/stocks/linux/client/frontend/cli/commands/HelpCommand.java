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
        } else if (commands.size() == 2) {
            printCommandHelp(commands.get(1));
        } else {
            System.out.println("Too many arguments!");
        }
    }

    @Override
    public void printHelp() {
        printGeneralHelp();
    }

    public void printGeneralHelp() {
        String help = "Stocks linux CLI client\n" +
                "Possible commands: \n" +
                "\n" +
                "\trefresh\t\t\tRefresh all data from the server\n" +
                "\tuser\t\t\tCommands regarding users\n" +
                "\tloc\t\t\t\tCommands regarding locations\n";

        System.out.println(help);
    }

    public void printCommandHelp(String command) {

    }
}
