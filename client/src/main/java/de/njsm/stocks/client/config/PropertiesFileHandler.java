package de.njsm.stocks.client.config;

import java.io.IOException;
import java.util.Properties;

public interface PropertiesFileHandler {

    void writePropertiesToFile(String fileName, Properties source) throws IOException;

    Properties readProperties(String fileName) throws IOException;
}
