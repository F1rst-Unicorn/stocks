package de.njsm.stocks.linux.client.frontend.cli;

import javassist.bytecode.ByteArray;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class Scanner {

    protected InputStream in;

    public Scanner(InputStream input) {
        in = input;
    }

    public String next() {
        StringBuffer result = new StringBuffer();
        byte[] buffer = new byte[32];
        int bytesRead;

        try {
            do {
                bytesRead = in.read(buffer);
                boolean newLineFound = false;
                int i;

                for (i = 0; i < bytesRead; i++) {
                    if (buffer[i] == (byte) '\n'){
                        newLineFound = true;
                        break;
                    }
                }

                if (newLineFound) {
                    result.append(new String(buffer, 0, i));
                    break;
                } else {
                    result.append(new String(buffer));
                }


            } while (bytesRead != -1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }


        return result.toString();
    }


}
