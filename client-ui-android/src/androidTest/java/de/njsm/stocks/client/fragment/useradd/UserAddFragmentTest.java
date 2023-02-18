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

package de.njsm.stocks.client.fragment.useradd;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.UserAddInteractor;
import de.njsm.stocks.client.business.entities.UserAddForm;
import de.njsm.stocks.client.navigation.Navigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class UserAddFragmentTest {

    private FragmentScenario<UserAddFragment> scenario;

    private UserAddInteractor interactor;

    private Navigator navigator;

    @Before
    public void setup() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        reset(navigator);
        reset(interactor);
        scenario = FragmentScenario.launchInContainer(UserAddFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(navigator);
        reset(interactor);
    }

    @Test
    public void uiIsShown() {
        onView(allOf(isDescendantOfA(withId(R.id.fragment_user_form_name)), withId(R.id.text_field_conflict_text_field))).check(matches(isDisplayed()));
    }

    @Test
    public void addingFoodIsPropagated() {
        UserAddForm form = getInput();

        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_user_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText(form.name()));
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(interactor).add(form);
        verify(navigator).back();
    }

    @Test
    public void clearingNameShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_user_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("some name"), clearText());

        onView(withId(R.id.fragment_user_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Test
    public void invalidNameCharacterShowsError() {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_user_form_name)),
                withClassName(is(TextInputEditText.class.getName()))
        )).perform(replaceText("John Doe"));

        onView(withId(R.id.fragment_user_form_name))
                .check(matches(hasDescendant(withText(R.string.error_wrong_name_format))));
    }

    @Test
    public void submittingWithoutNameShowsError() {
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_check)));

        verify(interactor, never()).add(any());
        onView(withId(R.id.fragment_user_form_name))
                .check(matches(hasDescendant(withText(R.string.error_may_not_be_empty))));
    }

    @Inject
    public void setInteractor(UserAddInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private UserAddForm getInput() {
        return UserAddForm.create("Joanna");
    }
}
