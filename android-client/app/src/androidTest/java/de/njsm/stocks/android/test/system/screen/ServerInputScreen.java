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
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public class ServerInputScreen extends AbstractScreen {

    public ServerInputScreen enterServerName(String name) {
        ViewInteraction hostnameTextField;
        hostnameTextField = onView(
                allOf(withId(R.id.fragment_server_url), isDisplayed()));
        hostnameTextField.perform(replaceText(name), closeSoftKeyboard());
        return this;
    }

    public QrScreen next() {
        ViewInteraction nextScreenButton = onView(withId(R.id.fragment_server_server_button));
        nextScreenButton.perform(click());
        return new QrScreen();
    }

    public static ServerInputScreen test() {
        return new ServerInputScreen();
    }
}
