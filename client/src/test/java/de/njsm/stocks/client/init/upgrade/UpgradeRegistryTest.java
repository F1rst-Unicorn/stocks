package de.njsm.stocks.client.init.upgrade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UpgradeRegistryTest {

    private UpgradeRegistry uut;

    @Before
    public void setup() throws Exception {
        List<Upgrader> upgraders = new LinkedList<>();
        upgraders.add(new MockUpgrader(Version.create("0.0.1"), Version.create("0.0.2")));
        upgraders.add(new MockUpgrader(Version.create("0.0.2"), Version.create("0.0.3")));
        upgraders.add(new MockUpgrader(Version.create("0.0.3"), Version.create("0.0.4")));
        upgraders.add(new MockUpgrader(Version.create("0.0.4"), Version.create("0.0.5")));
        upgraders.add(new MockUpgrader(Version.create("0.0.5"), Version.create("0.0.6")));
        upgraders.add(new MockUpgrader(Version.create("0.0.6"), Version.create("0.0.7")));
        uut = new UpgradeRegistry(upgraders);
    }

    @Parameterized.Parameter(0)
    public Version from;

    @Parameterized.Parameter(1)
    public Version to;

    @Parameterized.Parameter(2)
    public int result;

    @Parameterized.Parameters()
    public static Iterable<Object[]> getTables() {
        return Arrays.asList(
                new Object[][] {
                        {Version.create("0.0.1"), Version.create("0.0.3"), 2},
                        {Version.create("0.0.2"), Version.create("0.0.5"), 3},
                        {Version.create("0.0.6"), Version.create("0.0.7"), 1},
                        {Version.create("0.0.1"), Version.create("0.0.1"), 0},
                        {Version.create("0.0.1"), Version.create("0.0.7"), 6},
                        {Version.create("0.0.5"), Version.create("0.0.4"), 0},
                });
    }

    @Test
    public void testRangeQueries() throws Exception {

        List<Upgrader> output = uut.getUpgraders(from, to);

        assertEquals(result, output.size());
    }
}