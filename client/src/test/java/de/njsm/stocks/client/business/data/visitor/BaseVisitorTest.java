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