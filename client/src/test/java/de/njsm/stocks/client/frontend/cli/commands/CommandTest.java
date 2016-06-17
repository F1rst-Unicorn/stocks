package de.njsm.stocks.client.frontend.cli.commands;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.time.temporal.ValueRange;
import java.util.Date;

public class CommandTest {

    @Test
    public void validConstruction() throws ParseException {
        String input = "food add --d 31.12.2017 --n sausage --l 3-4 -esa";

        Command uut = Command.createCommand(input);

        Assert.assertEquals("food", uut.next());
        Assert.assertEquals("add", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());

        uut.reset();
        Assert.assertEquals("food", uut.next());
        Assert.assertEquals("add", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());
        Assert.assertEquals("", uut.next());

        Date date = uut.getParamDate('d');

        String sausage = uut.getParam('n');
        Assert.assertEquals("sausage", sausage);

        ValueRange r = uut.getParamRange('l');
        Assert.assertEquals(3, r.getMinimum());
        Assert.assertEquals(4, r.getMaximum());

        Assert.assertTrue(uut.hasArg('e'));
        Assert.assertTrue(uut.hasArg('s'));
        Assert.assertTrue(uut.hasArg('a'));
    }

    @Test(expected = ParseException.class)
    public void argumentWitoutParam() throws ParseException {
        String input = "food --d";
        Command uut = Command.createCommand(input);

    }

    @Test(expected = ParseException.class)
    public void argumentWithManyChars() throws ParseException {
        String input = "food --date";
        Command uut = Command.createCommand(input);

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

        ValueRange r = uut.getParamRange('d');

        Assert.assertEquals(5, r.getMinimum());
        Assert.assertEquals(12, r.getMaximum());
    }

}
