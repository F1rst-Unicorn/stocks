package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class ToStringVisitorTest {

    private SimpleDateFormat format;

    private ToStringVisitor uut;

    @Before
    public void setup() throws Exception {
        format = new SimpleDateFormat("dd.MM.yyyy");
        uut = new ToStringVisitor(format);
    }

    @Test
    public void getFoodItemString() throws Exception {
        String date = "01.01.2015";
        FoodItem item = new FoodItem(1, format.parse(date), 2, 3, 4, 5);

        String output = uut.visit(item, null);

        assertEquals("\t\t" + item.id + ": " + date, output);
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
        String date = "01.01.2015";
        Update input = new Update("Food", format.parse(date));

        String output = uut.visit(input, null);

        assertEquals("\t" + input.table + ": " + date, output);
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
