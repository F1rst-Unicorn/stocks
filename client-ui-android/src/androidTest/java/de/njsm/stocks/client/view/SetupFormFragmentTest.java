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

package de.njsm.stocks.client.view;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.business.entities.SetupState;
import de.njsm.stocks.client.navigation.SetupFormNavigator;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SetupFormFragmentTest {

    private FragmentScenario<SetupFormFragment> scenario;

    private SetupFormFragmentArgumentProvider fragmentArgumentProvider;

    private RegistrationBackend registrationBackend;

    private SetupFormNavigator setupFormNavigator;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(SetupFormFragment.class, new Bundle(), R.style.StocksTheme);
        scenario.onFragment(fragment -> ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().inject(this));
    }

    @After
    public void tearDown() {
        reset(fragmentArgumentProvider);
        reset(registrationBackend);
        reset(setupFormNavigator);
    }

    @Test
    public void argumentProviderIsCalledOnOpening() {
        scenario.onFragment(v -> verify(fragmentArgumentProvider).visit(eq(v), any(Bundle.class)));
    }

    @Test
    public void startingWithRegistrationFormInitialisesFields() {
        RegistrationForm registrationForm = getRegistrationForm();

        scenario.onFragment(v -> v.initialiseForm(registrationForm));

        onView(withId(R.id.fragment_setup_form_server_name)).check(
                matches(hasDescendant(withText(registrationForm.serverName())))
        );
        onView(withId(R.id.fragment_setup_form_ca_port)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.caPort()))))
        );
        onView(withId(R.id.fragment_setup_form_registration_port)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.registrationPort()))))
        );
        onView(withId(R.id.fragment_setup_form_server_port)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.serverPort()))))
        );
        onView(withId(R.id.fragment_setup_form_user_name)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.userName()))))
        );
        onView(withId(R.id.fragment_setup_form_user_id)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.userId()))))
        );
        onView(withId(R.id.fragment_setup_form_device_name)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.userDeviceName()))))
        );
        onView(withId(R.id.fragment_setup_form_device_id)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.userDeviceId()))))
        );
        onView(withId(R.id.fragment_setup_form_fingerprint)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.fingerprint()))))
        );
        onView(withId(R.id.fragment_setup_form_ticket)).check(
                matches(hasDescendant(withText(String.valueOf(registrationForm.ticket()))))
        );
    }

    @Test
    public void registeringWithAllFieldsSetStartsProgressBar() {
        RegistrationForm registrationForm = getRegistrationForm();
        scenario.onFragment(v -> v.initialiseForm(registrationForm));
        when(registrationBackend.register(registrationForm)).thenReturn(Observable.just(SetupState.GENERATING_KEYS));

        onView(withId(R.id.fragment_setup_form_button)).perform(scrollTo(), click());

        verify(registrationBackend).register(registrationForm);
        onView(withId(R.id.fragment_setup_form_status)).check(matches(withText(R.string.dialog_generating_key)));
        onView(withId(R.id.fragment_setup_form_progress)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.fragment_setup_form_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void showingRegistrationFailureOffersRetry() {
        RegistrationForm registrationForm = getRegistrationForm();
        scenario.onFragment(v -> v.initialiseForm(registrationForm));
        when(registrationBackend.register(registrationForm)).thenReturn(Observable.just(SetupState.GENERATING_KEYS_FAILED));

        onView(withId(R.id.fragment_setup_form_button)).perform(scrollTo(), click());

        verify(registrationBackend).register(registrationForm);
        onView(withId(R.id.fragment_setup_form_status)).check(matches(withText(R.string.dialog_generating_key_failed)));
        onView(withId(R.id.fragment_setup_form_progress)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.fragment_setup_form_button)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.fragment_setup_form_button)).check(matches(withText(R.string.dialog_retry)));
    }

    @Test
    public void successfulRegistrationNavigates() {
        RegistrationForm registrationForm = getRegistrationForm();
        scenario.onFragment(v -> v.initialiseForm(registrationForm));
        when(registrationBackend.register(registrationForm)).thenReturn(Observable.just(SetupState.SUCCESS));

        onView(withId(R.id.fragment_setup_form_button)).perform(scrollTo(), click());

        verify(setupFormNavigator).finishSetup();
    }

    @Test
    public void anyMissingFieldBlocksSubmission() {
        RegistrationForm registrationForm = getRegistrationForm();
        List<Integer> views = Arrays.asList(
                R.id.fragment_setup_form_server_name,
                R.id.fragment_setup_form_ca_port,
                R.id.fragment_setup_form_registration_port,
                R.id.fragment_setup_form_server_port,
                R.id.fragment_setup_form_user_name,
                R.id.fragment_setup_form_user_id,
                R.id.fragment_setup_form_device_name,
                R.id.fragment_setup_form_device_id,
                R.id.fragment_setup_form_fingerprint,
                R.id.fragment_setup_form_ticket
        );

        for (int view : views) {
            clearFieldAndCheckIfButtonIsDisabled(registrationForm, view);
        }
    }

    private void clearFieldAndCheckIfButtonIsDisabled(RegistrationForm registrationForm, int viewId) {
        scenario.onFragment(v -> v.initialiseForm(registrationForm));
        onView(allOf(
                isDescendantOfA(withId(viewId)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(scrollTo(), clearText());

        onView(withId(R.id.fragment_setup_form_button)).perform(scrollTo(), click());

        verifyNoInteractions(registrationBackend);
    }

    private RegistrationForm getRegistrationForm() {
        return RegistrationForm.builder()
                .serverName("test.example")
                .caPort(1409)
                .registrationPort(1410)
                .serverPort(1411)
                .userId(1412)
                .userName("username")
                .userDeviceId(1412)
                .userDeviceName("userdevicename")
                .fingerprint("fingerprint")
                .ticket("ticket")
                .build();
    }

    @Inject
    public void setFragmentArgumentProvider(SetupFormFragmentArgumentProvider fragmentArgumentProvider) {
        this.fragmentArgumentProvider = fragmentArgumentProvider;
    }

    @Inject
    public void setRegistrationBackend(RegistrationBackend registrationBackend) {
        this.registrationBackend = registrationBackend;
    }

    @Inject
    public void setSetupFragmentNavigator(SetupFormNavigator setupFormNavigator) {
        this.setupFormNavigator = setupFormNavigator;
    }
}
