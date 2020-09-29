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

package de.njsm.stocks.screen;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;

import java.util.function.Consumer;

import de.njsm.stocks.SystemTestSuite;

import static org.junit.Assert.fail;

public class AbstractScreen {

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    protected void performFlakyAction(Consumer<Void> action) {
        boolean done = false;
        int counter = 0;
        while (!done) {
            try {
                if (counter++ > SystemTestSuite.LOOP_BREAKER) {
                    fail("LOOP BREAKER triggered");
                }
                action.accept(null);
                done = true;
            } catch (NoMatchingViewException e) {}
        }
    }

    public AbstractScreen pressBack() {
        Espresso.pressBack();
        return this;
    }
}
