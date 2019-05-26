/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.business.data.Food;
import de.njsm.stocks.client.business.data.Location;
import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.business.data.visitor.ToStringVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ScreenWriterTest {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private ScreenWriter uut;

    private PrintStream mockStream;

    private ArgumentCaptor<String> captor;

    @Before
    public void setup() {
        mockStream = mock(PrintStream.class);
        ToStringVisitor stringBuilder = new ToStringVisitor(format);
        uut = new ScreenWriter(mockStream, stringBuilder);
        captor = ArgumentCaptor.forClass(String.class);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockStream);
    }

    @Test
    public void writingStringWorks() {
        String message = "This is a nice message";

        uut.println(message);

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingEmptyLocationListShowsConstantMessage() {
        String message = "No locations there...";

        uut.printLocations("Unused headline", Collections.emptyList());

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingLocationListWorks() {
        List<Location> list = new LinkedList<>();
        list.add(new Location(2, 7, "Basement"));
        list.add(new Location(3, 8, "Fridge"));
        String headline = "Some headline";
        String item1 = "\t" + list.get(0).id + ": " + list.get(0).name;
        String item2 = "\t" + list.get(1).id + ": " + list.get(1).name;

        uut.printLocations(headline, list);

        verify(mockStream, times(3)).println(captor.capture());
        assertEquals(headline, captor.getAllValues().get(0));
        assertEquals(item1, captor.getAllValues().get(1));
        assertEquals(item2, captor.getAllValues().get(2));
    }

    @Test
    public void printingEmptyFoodListShowsConstantMessage() {
        String message = "No food there...";

        uut.printFood("Unused headline", Collections.emptyList());

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingFoodListWorks() {
        List<Food> list = new LinkedList<>();
        list.add(new Food(2, 7, "Bread"));
        list.add(new Food(3, 8, "Beer"));
        String headline = "Some headline";
        String item1 = "\t" + list.get(0).id + ": " + list.get(0).name;
        String item2 = "\t" + list.get(1).id + ": " + list.get(1).name;

        uut.printFood(headline, list);

        verify(mockStream, times(3)).println(captor.capture());
        assertEquals(headline, captor.getAllValues().get(0));
        assertEquals(item1, captor.getAllValues().get(1));
        assertEquals(item2, captor.getAllValues().get(2));
    }

    @Test
    public void printingEmptyUserListGivesMessage() {
        String message = "No users there...";

        uut.printUsers("Unused headline", Collections.emptyList());

        verify(mockStream).println(message);
    }

    @Test
    public void printingUsersGivesUserList() {
        String headline = "some headline";
        List<User> users = new LinkedList<>();
        User user1 = new User(3, 8, "Jack");
        User user2 = new User(4, 9, "Juliette");
        users.add(user1);
        users.add(user2);

        uut.printUsers(headline, users);

        verify(mockStream).println(headline);
        verify(mockStream).println("\t" + user1.id + ": " + user1.name);
        verify(mockStream).println("\t" + user2.id + ": " + user2.name);
    }

    @Test
    public void printingEmptyDeviceListGivesMessage() {
        String message = "No devices there...";

        uut.printUserDeviceViews("Unused headline", Collections.emptyList());

        verify(mockStream).println(message);
    }

    @Test
    public void printingUserDevicesGivesList() {
        String headline = "some headline";
        List<UserDeviceView> users = new LinkedList<>();
        UserDeviceView dev1 = new UserDeviceView(3, 8, "Mobile", "Jack", 1);
        UserDeviceView dev2 = new UserDeviceView(4, 9, "Laptop", "Jack", 1);
        users.add(dev1);
        users.add(dev2);

        uut.printUserDeviceViews(headline, users);

        verify(mockStream).println(headline);
        verify(mockStream).println("\t" + dev1.id + ": " + dev1.user + "'s " + dev1.name);
        verify(mockStream).println("\t" + dev2.id + ": " + dev2.user + "'s " + dev2.name);
    }
}
