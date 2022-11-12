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

package de.njsm.stocks.client.fragment.eanlist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeEanNumberListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.EanNumberAddForm;
import de.njsm.stocks.client.business.entities.EanNumberForListing;
import de.njsm.stocks.client.navigation.EanNumberListNavigator;
import de.njsm.stocks.client.testdata.EanNumbersForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EanNumberListFragmentTest {

    private FragmentScenario<EanNumberListFragment> scenario;

    private FakeEanNumberListInteractor eanNumberListInteractor;

    private EanNumberListNavigator eanNumberListNavigator;

    private Synchroniser synchroniser;

    @Before
    public void setUp() {
        Intents.init();
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        when(eanNumberListNavigator.getFood(any())).thenReturn(() -> 42);
        scenario = FragmentScenario.launchInContainer(EanNumberListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(synchroniser);
        reset(eanNumberListNavigator);
        Intents.release();
    }

    @Test
    public void dataIsListed() {
        eanNumberListInteractor.setData(EanNumbersForListing.generate());

        for (EanNumberForListing item : EanNumbersForListing.generate()) {
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withText(item.eanNumber()))));
        }
    }

    @Test
    public void addingPropagatesToAddInteractor() {
        EanNumberAddForm expected = EanNumberAddForm.create(() -> 42, "123");
        Instrumentation.ActivityResult activityResult = returnDataUponScanning(expected);
        Intents.intending(IntentMatchers.hasAction("com.google.zxing.client.android.SCAN")).respondWith(activityResult);

        onView(withId(R.id.template_swipe_list_fab)).perform(click());

        assertEquals(expected, eanNumberListInteractor.getEanNumberAddForm());
    }

    private Instrumentation.ActivityResult returnDataUponScanning(EanNumberAddForm eanNumberAddForm) {
        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT", eanNumberAddForm.eanNumber());
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
    }

    @Inject
    public void setEanNumberListInteractor(FakeEanNumberListInteractor EanNumberListInteractor) {
        this.eanNumberListInteractor = EanNumberListInteractor;
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    public void setEanNumberListNavigator(EanNumberListNavigator EanNumberListNavigator) {
        this.eanNumberListNavigator = EanNumberListNavigator;
    }
}
