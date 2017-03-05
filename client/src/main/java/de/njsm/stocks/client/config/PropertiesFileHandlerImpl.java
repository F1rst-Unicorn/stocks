package de.njsm.stocks.client.config;


import java.io.*;
import java.util.Properties;

public class PropertiesFileHandlerImpl implements PropertiesFileHandler {

    @Override
    public void writePropertiesToFile(String fileName, Properties source) throws IOException {
        FileWriter targetFile = new FileWriter(fileName);
        BufferedWriter fileWriter = new BufferedWriter(targetFile);
        source.store(fileWriter, "stocks configuration");
        fileWriter.close();
    }

    @Override
    public Properties readProperties(String fileName) throws IOException {
        FileReader sourceFile = new FileReader(fileName);
        BufferedReader source = new BufferedReader(sourceFile);
        Properties p = new Properties();
        p.load(source);
        source.close();
        return p;
    }
}
