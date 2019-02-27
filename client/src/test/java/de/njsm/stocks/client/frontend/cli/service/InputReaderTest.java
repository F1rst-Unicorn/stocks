package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.service.TimeProvider;
import org.jline.reader.LineReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InputReaderTest {

    private InputReader uut;

    private LineReader inMock;

    private PrintStream outMock;

    private TimeProvider timeMock;

    private ArgumentCaptor<String> captor;

    @Before
    public void setup() {
        inMock = mock(LineReader.class);
        outMock = mock(PrintStream.class);
        timeMock = mock(TimeProvider.class);
        when(timeMock.getTime()).thenReturn(0L);
        uut = new InputReader(outMock, inMock, timeMock, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        captor = ArgumentCaptor.forClass(String.class);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(inMock);
        verifyNoMoreInteractions(outMock);
    }

    @Test
    public void testGettingString() {
        String prompt = "some prompt";
        String expectedOutput = "user input";
        when(inMock.readLine(prompt)).thenReturn(expectedOutput);

        String output = uut.next(prompt);

        verify(inMock).readLine(prompt);
        assertEquals(expectedOutput, output);
    }

    @Test
    public void readingNullGivesLineBreak() {
        when(inMock.readLine("")).thenReturn(null);

        String output = uut.next("");

        assertEquals("\n", output);
        verify(inMock).readLine("");
    }

    @Test
    public void returnInputOnValidName() {
        String expectedOutput = "user input";
        when(inMock.readLine("")).thenReturn(expectedOutput);

        String output = uut.nextName("");

        verify(inMock).readLine("");
        assertEquals(expectedOutput, output);
    }

    @Test
    public void askAgainOnInvalidNameInput() {
        String invalidName = "user$input";
        String validName = "user input";
        when(inMock.readLine(anyString())).thenReturn(invalidName, invalidName, validName);

        String output = uut.nextName("");

        verify(inMock, times(3)).readLine(anyString());
        assertEquals(validName, output);
    }

    @Test
    public void enteringValidNumberWorks() {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        when(inMock.readLine("")).thenReturn(input);

        int output = uut.nextInt("");

        verify(inMock).readLine("");
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringInvalidNumberAsksAgain() {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        String invalidNumber = "invalid";
        String errorMessage = "That's not a number. Try again: ";
        when(inMock.readLine("")).thenReturn(invalidNumber, invalidNumber, input);

        int output = uut.nextInt("");

        verify(outMock, times(2)).print(captor.capture());
        verify(inMock, times(3)).readLine(anyString());
        assertEquals(errorMessage, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringValidNumberWithDefaultWorks() {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        when(inMock.readLine(anyString())).thenReturn(input);

        int output = uut.nextInt("", 5);

        verify(inMock).readLine(anyString());
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringInvalidNumberWithDefaultAsksAgain() {
        int inputNumber = 53;
        String input = String.valueOf(inputNumber);
        String invalidNumber = "invalid";
        String errorMessage = "That's not a number. Try again: ";
        when(inMock.readLine(any(String.class))).thenReturn(invalidNumber, invalidNumber, input);

        int output = uut.nextInt("", 5);

        verify(outMock, times(2)).print(captor.capture());
        verify(inMock, times(3)).readLine(any(String.class));
        assertEquals(errorMessage, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(inputNumber, output);
    }

    @Test
    public void enteringNothingWithDefaultGivesDefault() {
        int expectedOutput = 5;
        when(inMock.readLine(anyString())).thenReturn("");

        int output = uut.nextInt("", expectedOutput);

        verify(inMock).readLine(anyString());
        assertEquals(expectedOutput, output);
    }

    @Test
    public void enteringDateWorks() {
        String prompt = "some prompt";
        String dateInput = "11.09.1991";
        when(inMock.readLine(prompt)).thenReturn(dateInput);

        LocalDate output = uut.nextDate(prompt);

        verify(inMock).readLine(prompt);
        assertEquals(LocalDate.parse("1991-09-11"), output);
    }

    @Test
    public void enteringInvalidDateAsksAgain() {
        String prompt = "some prompt";
        String dateInput = "11.09.1991";
        String invalidDate = "fhdlasfa";
        String errorMessage = "Invalid date. Try again: ";
        when(inMock.readLine(anyString())).thenReturn(invalidDate, invalidDate, dateInput);

        LocalDate output = uut.nextDate(prompt);

        verify(inMock, times(3)).readLine(captor.capture());
        assertEquals(LocalDate.parse("1991-09-11"), output);
        assertEquals(prompt, captor.getAllValues().get(0));
        assertEquals(errorMessage, captor.getAllValues().get(1));
        assertEquals(errorMessage, captor.getAllValues().get(2));
    }

    @Test
    public void testGettingYes() {
        String prompt = " (y/N): ";
        when(inMock.readLine(prompt)).thenReturn("y");

        assertTrue(uut.getYesNo());
        verify(inMock).readLine(prompt);
    }

    @Test
    public void testGettingNo() {
        String prompt = " (y/N): ";
        when(inMock.readLine(prompt)).thenReturn("N");

        assertFalse(uut.getYesNo());
        verify(inMock).readLine(prompt);
    }

    @Test
    public void gettingYesWithInvalidInputReturnsNo() {
        String prompt = " (y/N): ";
        when(inMock.readLine(prompt)).thenReturn("fdsa");

        assertFalse(uut.getYesNo());
        verify(inMock).readLine(prompt);
    }

    @Test
    public void parsingValidDateWorks() throws ParseException {
        String date = "11.09.1991";

        LocalDate output = uut.parseDate(date);

        assertEquals(LocalDate.parse("1991-09-11"), output);
    }

    @Test
    public void parsingRelativeDayWorks() throws ParseException {
        int dayOffset = 5;
        String date = "+" + dayOffset + "d";

        LocalDate output = uut.parseDate(date);

        assertEquals(LocalDate.ofEpochDay(0).plus(Period.ofDays(5)), output);
    }

    @Test
    public void parsingRelativeMonthWorks() throws ParseException {
        int monthOffset = 5;
        String date = "+" + monthOffset + "m";

        LocalDate output = uut.parseDate(date);

        assertEquals(LocalDate.ofEpochDay(0).plus(Period.ofDays(5 * 30)), output);
    }

    @Test
    public void parsingRelativeWithoutNumberGivesException() {
        try {
            uut.parseDate("+m");
            fail();
        } catch (ParseException e) {

        }
    }

    @Test
    public void parsingRelativeWithUnknownUnitGivesException() {
        try {
            uut.parseDate("+4x");
            fail();
        } catch (ParseException e) {

        }
    }

    @Test
    public void parsingGarbageGivesException() {
        try {
            uut.parseDate("ihfospajf");
            fail();
        } catch (ParseException e) {

        }
    }
}
