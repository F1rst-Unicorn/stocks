package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.cli.commands.AggregatedCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CliMainHandler implements MainHandler {

    private static final Logger LOG = LogManager.getLogger(CliMainHandler.class);

    private final AggregatedCommandHandler m;
    private InputReader reader;

    CliMainHandler(AggregatedCommandHandler m, InputReader reader) {
        this.m = m;
        this.reader = reader;
    }

    @Override
    public void run(String[] args) {
        boolean endRequested = false;
        Command command;

        if (args.length > 0) {
            try {
                command = Command.createCommand(args);
                m.handle(command);
            } catch (ParseException e) {
                LOG.error("Could not parse command", e);
            }
        } else {
            while (!endRequested) {
                String input = reader.next("stocks $ ");

                switch (input) {
                    case "quit":
                        endRequested = true;
                        break;
                    case "":
                    case "\n":
                        break;
                    default:
                        try {
                            command = Command.createCommand(input);
                            m.handle(command);
                        } catch (ParseException e) {
                            LOG.error("Could not parse command", e);
                        }
                }
            }
        }
        reader.shutdown();
    }
}
