package de.njsm.stocks.client.business.data.visitor;

import org.junit.Before;
import org.junit.Test;

public class BaseVisitorTest {

    private BaseVisitor<Void, Void> uut;

    @Before
    public void setup() {
        uut = new BaseVisitor<>();
    }

    @Test(expected = RuntimeException.class)
    public void foodIsNotImplemented() {
        uut.food(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void locationIsNotImplemented() {
        uut.location(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void foodItemIsNotImplemented() {
        uut.foodItem(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void userDeviceIsNotImplemented() {
        uut.userDevice(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void userIsNotImplemented() {
        uut.user(null, null);
    }
}