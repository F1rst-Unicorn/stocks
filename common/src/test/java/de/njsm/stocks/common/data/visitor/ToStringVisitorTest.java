package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.Before;
import org.junit.Test;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

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

    @Test
    public void getUserDeviceViewString() throws Exception {
        UserDeviceView input = new UserDeviceView(2, "Mobile", "Jack", 1);

        String output = uut.visit(input, null);

        assertEquals("\t" + input.id + ": " + input.user + "'s " + input.name, output);
    }
}
