package de.njsm.stocks.client.frontend.cli;

import java.io.PrintStream;

public class ScreenWriter {

    private PrintStream outputStream;

    public ScreenWriter(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void println(String text) {
        outputStream.println(text);
    }

}
