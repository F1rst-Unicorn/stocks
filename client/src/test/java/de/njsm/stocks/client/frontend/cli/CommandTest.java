package de.njsm.stocks.client.frontend.cli;

import org.junit.Assert;
import org.junit.Test;

import de.njsm.stocks.client.exceptions.ParseException;
import java.time.temporal.ValueRange;

public class CommandTest {

    @Test
    public void validConstruction() throws ParseException {
        String input = "food add --n sausage --l 3-4 -esa";

        Command uut = Command.createCommand(input);

        Assert.assertEquals("food", uut.next());
        Assert.assertEquals("add", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());

        String sausage = uut.getParam('n');
        Assert.assertEquals("sausage", sausage);

        ValueRange r = uut.getParamRange('l');
        Assert.assertEquals(3, r.getMinimum());
        Assert.assertEquals(4, r.getMaximum());

        Assert.assertTrue(uut.hasArg('e'));
        Assert.assertTrue(uut.hasArg('s'));
        Assert.assertTrue(uut.hasArg('a'));
    }

    @Test
    public void constructionFromArrayWorks() throws Exception {
        String[] input = new String[] {
                "food",
                "add",
                "--n",
                "sausage",
                "--l",
                "3-4",
                "-esa"
        };

        Command uut = Command.createCommand(input);

        Assert.assertEquals("food", uut.next());
        Assert.assertEquals("add", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());

        String sausage = uut.getParam('n');
        Assert.assertEquals("sausage", sausage);

        ValueRange r = uut.getParamRange('l');
        Assert.assertEquals(3, r.getMinimum());
        Assert.assertEquals(4, r.getMaximum());

        Assert.assertTrue(uut.hasArg('e'));
        Assert.assertTrue(uut.hasArg('s'));
        Assert.assertTrue(uut.hasArg('a'));
    }

    @Test
    public void resettingCommandWorks() throws Exception {
        String input = "food add";
        Command uut = Command.createCommand(input);

        while (uut.hasNext()) {
            uut.next();
        }
        uut.reset();

        Assert.assertEquals("food", uut.next());
        Assert.assertEquals("add", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
    }

    @Test(expected = ParseException.class)
    public void argumentWitoutParam() throws ParseException {
        String input = "food --d";
        Command.createCommand(input);
    }

    @Test(expected = ParseException.class)
    public void argumentWithManyChars() throws ParseException {
        String input = "food --date";
        Command.createCommand(input);
    }

    @Test
    public void validInt() throws ParseException {
        String input = "food --d 5";
        Command uut = Command.createCommand(input);

        Assert.assertEquals(5, uut.getParamInt('d'));
    }

    @Test(expected = ParseException.class)
    public void invalidInt() throws ParseException {
        String input = "food --d 5i";
        Command uut = Command.createCommand(input);

        Assert.assertEquals(5, uut.getParamInt('d'));
    }

    @Test
    public void validSingletonRange() throws ParseException {
        String input = "food --d 5";
        Command uut = Command.createCommand(input);

        ValueRange r = uut.getParamRange('d');

        Assert.assertEquals(5, r.getMinimum());
        Assert.assertEquals(5, r.getMaximum());
    }

    @Test
    public void validRange() throws ParseException {
        String input = "food --d 5-12";
        Command uut = Command.createCommand(input);

        ValueRange r = uut.getParamRange('d');

        Assert.assertEquals(5, r.getMinimum());
        Assert.assertEquals(12, r.getMaximum());
    }

    @Test(expected = ParseException.class)
    public void invalidRange() throws ParseException {
        String input = "food --d 5-12i";
        Command uut = Command.createCommand(input);

        uut.getParamRange('d');
    }

    @Test
    public void testToString() throws Exception {
        String input = "food add --n sausage --l 3-4 -esa";
        Command uut = Command.createCommand(input);
        String expectedOutput = "food add -a -e -l=3-4 -n=sausage -s";

        String output = uut.toString();

        Assert.assertEquals(expectedOutput, output);
    }
}
