package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.exceptions.ParseException;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {

    private final Map<Character, String> arguments;
    private final List<String> command;
    private Iterator<String> commandIt;

    public static Command createCommand(String input) throws ParseException {
        Command result = new Command();
        List<String> words = result.parseCommand(input);
        result.fillCommand(words);
        return result;
    }

    public static Command createCommand(String[] input) throws ParseException {
        Command result = new Command();
        List<String> words = result.parseCommand(input);
        result.fillCommand(words);
        return result;
    }

    public boolean hasNext() {
        return commandIt.hasNext();
    }

    public String next() {
        if (commandIt.hasNext()){
            return commandIt.next();
        } else {
            return "";
        }
    }

    public void reset() {
        commandIt = command.iterator();
    }

    public boolean hasArg(char c) {
        return arguments.containsKey(c);
    }

    public String getParam(char c) {
        return arguments.get(c);
    }

    public int getParamInt(char c) throws ParseException {
        String value = arguments.get(c);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParseException("Parameter for -" + c + " is not a number");
        }
    }

    public Date getParamDate(char c) throws ParseException {
        String value = arguments.get(c);
        return InputReader.parseDate(value);
    }

    public ValueRange getParamRange(char c) throws ParseException {
        String value = arguments.get(c);
        Pattern p = Pattern.compile("^[0-9]+(-[0-9]+)?$");
        Matcher m = p.matcher(value);
        if (m.find()) {
            int minusIndex = value.indexOf('-');
            if (minusIndex == -1) {
                int number = getParamInt(c);
                return ValueRange.of(number, number);
            } else {
                int inf = Integer.parseInt(value.substring(0, minusIndex));
                int sup = Integer.parseInt(value.substring(minusIndex+1, value.length()));
                return ValueRange.of(inf, sup);
            }
        } else {
            throw new ParseException("Not a range: " + value);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String c : command) {
            buf.append(c);
            buf.append(" ");
        }

        for (Map.Entry<Character, String> e : arguments.entrySet()) {
            buf.append("-");
            buf.append(e.getKey());
            if (!e.getValue().equals("")) {
                buf.append("=");
                buf.append(e.getValue());
            }
            buf.append(" ");
        }

        return buf.toString();
    }

    protected Command() {
        arguments = new TreeMap<>();
        command = new LinkedList<>();
    }

    protected List<String> parseCommand(String command) {
        String[] commands = command.split(" ");
        LinkedList<String> result = new LinkedList<>();
        Collections.addAll(result, commands);
        return result;
    }

    protected List<String> parseCommand(String[] commandArray) {
        LinkedList<String> result = new LinkedList<>();
        Collections.addAll(result, commandArray);
        return result;
    }

    protected void fillCommand(List<String> words) throws ParseException {
        Iterator<String> it = words.iterator();
        while (it.hasNext()) {
            String word = it.next();
            if (word.length() > 1 && word.charAt(0) == '-') {
                if (word.length() > 2 && word.charAt(1) == '-') {
                    handleParameterisedArgument(it, word);
                } else {
                    handleArgument(word);
                }
            } else {
                command.add(word);
            }
        }
        commandIt = command.iterator();
    }

    protected void handleArgument(String word) {
        for (int i = 1; i < word.length(); i++) {
            arguments.put(word.charAt(i), "");
        }
    }

    protected void handleParameterisedArgument(Iterator<String> it, String word) throws ParseException  {
        if (word.length() != 3) {
            throw new ParseException("Only one argument allowed at once: " + word);
        }

        if (!it.hasNext()) {
            throw new ParseException("Missing parameter for " + word);
        }

        String parameter = it.next();
        arguments.put(word.charAt(2), parameter);
    }


}
