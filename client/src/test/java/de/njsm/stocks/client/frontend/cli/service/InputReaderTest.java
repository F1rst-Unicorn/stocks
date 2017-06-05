package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.service.TimeProvider;
import jline.console.ConsoleReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InputReaderTest {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private InputReader uut;

    private ConsoleReader inMock;

    private PrintStream outMock;

    private TimeProvider timeMock;

    private ArgumentCaptor<String> captor;

    @Before
    public void setup() throws Exception {
        inMock = mock(ConsoleReader.class);
        outMock = mock(PrintStream.class);
        timeMock = mock(TimeProvider.class);
        when(timeMock.getTime()).thenReturn(0L);
        uut = new InputReader(inMock, outMock, timeMock);
        captor = ArgumentCaptor.forClass(String.class);
    }

    @After
    public void tearDown() throws Exception {
        verify(inMock).setHistoryEnabled(true);
        verify(inMock).setHistory(any());
        verifyNoMoreInteractions(inMock);
        verifyNoMoreInteractions(outMock);
    }

    @Test
    public void testGettingString() throws Exception {
        String prompt = "some prompt";
        String expectedOutput = "user input";
        when(inMock.readLine()).thenReturn(expectedOutput);

        String output = uut.next(prompt);

        verify(inMock).setPrompt(captor.capture());
        verify(inMock).readLine();
        assertEquals(prompt, captor.getValue());
        assertEquals(expectedOutput, output);
    }

    @Test
    public void readingNullGivesLineBreak() throws Exception {
        when(inMock.readLine()).thenReturn(null);

        String output = uut.next("");

        assertEquals("\n", output);
        verify(inMock).readLine();
        verify(inMock).setPrompt("");
    }

    @Test
    public void returnInputOnValidName() throws Exception {
        String expectedOutput = "user input";
        when(inMock.readLine()).thenReturn(expectedOutput);

        String output = uut.nextName("");

        verify(inMock).setPrompt(anyString());
        verify(inMock).readLine();
        assertEquals(expectedOutput, output);
    }

    @Test
    public void returnEmptyStringOnIoProblem() throws Exception {
        when(inMock.readLine()).thenThrow(new IOException("error"));

        String output = uut.next("");

        verify(inMock).setPrompt(anyString());
        verify(inMock).readLine();
        assertEquals("", output);
    }


    @Test
    public void askAgainOnInvalidNameInput() throws Exception {
        String invalidName = "user$input";
        String validName = "user input";
        when(inMock.readLine()).thenReturn(invalidName, invalidName, validName);

        String output = uut.nextName("");

        verify(inMock, times(3)).setPrompt(anyString());
        verify(inMock, times(3)).readLine();
        assertEquals(validName, output);
    }

    @Test
    public void enteringValidNumberWorks() throws Exception {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        when(inMock.readLine()).thenReturn(input);

        int output = uut.nextInt("");

        verify(inMock).setPrompt(anyString());
        verify(inMock).readLine();
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringInvalidNumberAsksAgain() throws Exception {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        String invalidNumber = "invalid";
        String errorMessage = "That's not a number. Try again: ";
        when(inMock.readLine()).thenReturn(invalidNumber, invalidNumber, input);

        int output = uut.nextInt("");

        verify(outMock, times(2)).print(captor.capture());
        verify(inMock, times(3)).setPrompt(anyString());
        verify(inMock, times(3)).readLine();
        assertEquals(errorMessage, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringValidNumberWithDefaultWorks() throws Exception {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        when(inMock.readLine()).thenReturn(input);

        int output = uut.nextInt("", 5);

        verify(inMock).setPrompt(anyString());
        verify(inMock).readLine();
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringInvalidNumberWithDefaultAsksAgain() throws Exception {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        String invalidNumber = "invalid";
        String errorMessage = "That's not a number. Try again: ";
        when(inMock.readLine()).thenReturn(invalidNumber, invalidNumber, input);

        int output = uut.nextInt("", 5);

        verify(outMock, times(2)).print(captor.capture());
        verify(inMock, times(3)).setPrompt(anyString());
        verify(inMock, times(3)).readLine();
        assertEquals(errorMessage, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringNothingWithDefaultGivesDefault() throws Exception {
        int expectedOutput = 5;
        when(inMock.readLine()).thenReturn("");

        int output = uut.nextInt("", expectedOutput);

        verify(inMock).setPrompt(anyString());
        verify(inMock).readLine();
        assertEquals(expectedOutput, output);
    }

    @Test
    public void enteringDateWorks() throws Exception {
        String prompt = "some prompt";
        String dateInput = "11.09.1991";
        when(inMock.readLine()).thenReturn(dateInput);

        Date output = uut.nextDate(prompt);

        verify(inMock).setPrompt(prompt);
        verify(inMock).readLine();
        assertEquals(dateInput, format.format(output));
    }

    @Test
    public void enteringInvalidDateAsksAgain() throws Exception {
        String prompt = "some prompt";
        String dateInput = "11.09.1991";
        String invalidDate = "fhdlasfa";
        String errorMessage = "Invalid date. Try again: ";
        when(inMock.readLine()).thenReturn(invalidDate, invalidDate, dateInput);

        Date output = uut.nextDate(prompt);

        verify(inMock, times(3)).setPrompt(captor.capture());
        verify(inMock, times(3)).readLine();
        assertEquals(dateInput, format.format(output));
        assertEquals(prompt, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(errorMessage, captor.getAllValues().get(2));
    }

    @Test
    public void testGettingYes() throws Exception {
        String prompt = " (y/N): ";
        when(inMock.readLine()).thenReturn("y");

        assertTrue(uut.getYesNo());
        verify(inMock).setPrompt(prompt);
        verify(inMock).readLine();
    }

    @Test
    public void testGettingNo() throws Exception {
        String prompt = " (y/N): ";
        when(inMock.readLine()).thenReturn("N");

        assertFalse(uut.getYesNo());
        verify(inMock).setPrompt(prompt);
        verify(inMock).readLine();
    }

    @Test
    public void gettingYesWithInvalidInputReturnsNo() throws Exception {
        String prompt = " (y/N): ";
        when(inMock.readLine()).thenReturn("fdsa");

        assertFalse(uut.getYesNo());
        verify(inMock).setPrompt(prompt);
        verify(inMock).readLine();
    }

    @Test
    public void parsingValidDateWorks() throws Exception {
        String date = "11.09.1991";
        Date expectedOutput = format.parse(date);

        Date output = uut.parseDate(date);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void parsingRelativeDayWorks() throws Exception {
        int dayOffset = 5;
        String date = "+" + dayOffset + "d";
        Date expectedOutput = new Date(dayOffset * 1000L * 60L * 60L * 24L);

        Date output = uut.parseDate(date);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void parsingRelativeMonthWorks() throws Exception {
        int monthOffset = 5;
        String date = "+" + monthOffset + "m";
        Date expectedOutput = new Date(monthOffset * 1000L * 60L * 60L * 24L * 30L);

        Date output = uut.parseDate(date);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void parsingRelativeWithoutNumberGivesException() throws Exception {
        try {
            uut.parseDate("+m");
            fail();
        } catch (ParseException e) {

        }
    }

    @Test
    public void parsingRelativeWithUnknownUnitGivesException() throws Exception {
        try {
            uut.parseDate("+4x");
            fail();
        } catch (ParseException e) {

        }
    }

    @Test
    public void parsingGarbageGivesException() throws Exception {
        try {
            uut.parseDate("ihfospajf");
            fail();
        } catch (ParseException e) {

        }
    }
}
