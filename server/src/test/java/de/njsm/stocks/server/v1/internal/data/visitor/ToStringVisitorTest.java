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

package de.njsm.stocks.server.v1.internal.data.visitor;

import de.njsm.stocks.server.v1.internal.data.*;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class ToStringVisitorTest {

    private DateTimeFormatter format;

    private ToStringVisitor uut;

    @Before
    public void setup() throws Exception {
        format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.of("UTC"));
        uut = new ToStringVisitor(format);
    }

    @Test
    public void getFoodItemString() throws Exception {
        FoodItem item = new FoodItem(1, Instant.parse("2015-01-01T00:00:00Z"), 2, 3, 4, 5);

        String output = uut.visit(item, null);

        assertEquals("\t\t" + item.id + ": 01.01.2015", output);
    }

    @Test
    public void getFoodString() throws Exception {
        Food input = new Food(2, "Beer");

        String output = uut.visit(input, null);

        assertEquals("\t" + input.id + ": " + input.name, output);
    }

    @Test
    public void getUserString() throws Exception {
        User input = new User(2, "Jack");

        String output = uut.visit(input, null);

        assertEquals("\t" + input.id + ": " + input.name, output);
    }

    @Test
    public void userDeviceReturnsNull() throws Exception {
        UserDevice input = new UserDevice(2, "Mobile", 1);

        String output = uut.visit(input, null);

        assertEquals(null, output);
    }

    @Test
    public void getLocationString() throws Exception {
        Location input = new Location(2, "Jack");

        String output = uut.visit(input, null);

        assertEquals("\t" + input.id + ": " + input.name, output);
    }

    @Test
    public void getUpdateString() throws Exception {
        Update input = new Update("Food", Instant.parse("2015-01-01T00:00:00Z"));

        String output = uut.visit(input, null);

        assertEquals("\t" + input.table + ": 01.01.2015", output);
    }

    @Test
    public void ticketReturnsNull() throws Exception {
        Ticket input = new Ticket(2, "Mobile", "somePemFile");

        String output = uut.visit(input, null);

        assertEquals(null, output);
    }

}
