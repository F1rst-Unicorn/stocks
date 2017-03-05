package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.config.Configuration;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import jline.console.history.History;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EnhancedInputReader extends InputReader {

    protected ConsoleReader reader;

    public EnhancedInputReader(InputStream input) {
        super(input);
        try {
            reader = new ConsoleReader();

            History file = new FileHistory(new File(Configuration.STOCKS_HOME + "/history"));
            reader.setHistory(file);
            reader.setHistoryEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String next(String prompt) {
        reader.setPrompt(prompt);
        try {
            return reader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (reader.getHistory() instanceof FileHistory){
            try {
                ((FileHistory) reader.getHistory()).flush();
            } catch (IOException e) {
                System.out.println("History not saved: " + e.getMessage());
            }
        }
    }
}
