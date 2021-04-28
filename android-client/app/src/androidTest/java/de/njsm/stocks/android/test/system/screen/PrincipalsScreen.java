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

package de.njsm.stocks.android.test.system.screen;


import androidx.test.espresso.ViewInteraction;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.core.AllOf.allOf;

public class PrincipalsScreen {

    public PrincipalsScreen assertUser(String name) {
        onView(allOf(withId(R.id.fragment_principals_user_name), isDisplayed()))
                .check(matches(withText(name)));
        return this;
    }

    public PrincipalsScreen assertDevice(String device) {
        onView(allOf(withId(R.id.fragment_principals_device_name), isDisplayed()))
                .check(matches(withText(device)));
        return this;
    }

    public PrincipalsScreen assertUserId(int id) {
        onView(allOf(withId(R.id.fragment_principals_user_id), isDisplayed()))
                .check(matches(withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertDeviceId(int id) {
        onView(allOf(withId(R.id.fragment_principals_device_id), isDisplayed()))
                .check(matches(withText(String.valueOf(id))));
        return this;
    }

    public PrincipalsScreen assertFingerPrint(String fingerprint) {
        onView(allOf(withId(R.id.fragment_principals_fingerprint), isDisplayed()))
                .check(matches(withText(fingerprint)));
        return this;
    }

    public PrincipalsScreen assertTicket(String ticket) {
        onView(allOf(withId(R.id.fragment_principals_ticket), isDisplayed()))
                .check(matches(withText(ticket)));
        return this;
    }

    public OutlineScreen submit() {

        ViewInteraction appCompatTextView3 = onView(withId(R.id.fragment_principals_button));
        appCompatTextView3.perform(closeSoftKeyboard(), click());
        return new OutlineScreen();
    }
}
