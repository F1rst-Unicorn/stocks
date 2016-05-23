package de.njsm.stocks.linux.client.frontend.cli;

import java.io.IOException;
import java.io.InputStream;

public class InputReader {

    protected InputStream in;

    public InputReader(InputStream input) {
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

    public String nextName() {
        String result;

        result = next();

        while (! isNameValid(result)) {
            System.out.print("Name may not contain '$' or '='. Try again: ");
            result = next();
        }
        return result;
    }

    public int nextInt(int defaultResult) {
        String input;
        int result;
        do {
            input = next();
            if (input.equals("")){
                result = defaultResult;
            } else {
                try {
                    result = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    result = -1;
                    System.out.print("That's not a number. Try again: ");
                }
            }
        } while (result == -1);
        return result;
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

}
