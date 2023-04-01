/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.fragment.ticketshow;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.TicketDisplayInteractor;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.navigation.TicketShowNavigator;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class TicketShowFragmentTest {

    private FragmentScenario<TicketShowFragment> scenario;

    private TicketDisplayInteractor interactor;

    private TicketShowNavigator navigator;

    private @NonNull BehaviorSubject<RegistrationForm> prefilledFormData;

    private Application application;

    @Before
    public void setup() {
        Intents.init();
        application = ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext());
        application.getDaggerRoot().inject(this);
        reset(navigator);
        reset(interactor);
        Id<UserDevice> id = IdImpl.create(42);
        when(navigator.getId(any())).thenReturn(id);
        prefilledFormData = BehaviorSubject.createDefault(getInput());
        when(interactor.getRegistrationFormFor(eq(id))).thenReturn(prefilledFormData);
        scenario = FragmentScenario.launchInContainer(TicketShowFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(interactor);
        Intents.release();
    }

    @Test
    public void uiIsShown() {
        RegistrationForm registrationForm = getInput();

        onView(withId(R.id.fragment_ticket_show_text)).check(matches(withText(
                application.getString(R.string.hint_servername) + ": " + registrationForm.serverName() + "\n"
                        + application.getString(R.string.title_caport) + ": " + registrationForm.caPort() + "\n"
                        + application.getString(R.string.title_registration_port) + ": " + registrationForm.registrationPort() + "\n"
                        + application.getString(R.string.title_server_port) + ": " + registrationForm.serverPort() + "\n"
                        + application.getString(R.string.hint_username) + ": " + registrationForm.userName() + "\n"
                        + application.getString(R.string.hint_device_name) + ": " + registrationForm.userDeviceName() + "\n"
                        + application.getString(R.string.hint_user_id) + ": " + registrationForm.userId() + "\n"
                        + application.getString(R.string.hint_device_id) + ": " + registrationForm.userDeviceId() + "\n"
                        + application.getString(R.string.hint_fingerprint) + ": " + registrationForm.fingerprint() + "\n"
                        + application.getString(R.string.hint_ticket) + ": " + registrationForm.ticket()
        )));
    }

    @Test
    public void sharingWorks() {
        RegistrationForm form = getInput();

        onView(withId(R.id.fragment_ticket_show_share)).perform(click());

        Intents.intended(Matchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_SEND),
                IntentMatchers.hasExtra(Intent.EXTRA_TEXT, form.toQrString()),
                IntentMatchers.hasType("text/plain")
        ));
    }

    @Inject
    public void setInteractor(TicketDisplayInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    public void setNavigator(TicketShowNavigator navigator) {
        this.navigator = navigator;
    }

    private RegistrationForm getInput() {
        return RegistrationForm.builder()
                .serverName("serverName")
                .caPort(10910)
                .registrationPort(10911)
                .serverPort(10912)
                .userName("userName")
                .userId(1)
                .userDeviceName("userDeviceName")
                .userDeviceId(2)
                .fingerprint("fingerprint")
                .ticket("ticket")
                .build();
    }
}
