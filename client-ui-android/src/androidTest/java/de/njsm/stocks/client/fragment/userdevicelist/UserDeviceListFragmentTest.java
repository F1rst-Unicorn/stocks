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

package de.njsm.stocks.client.fragment.userdevicelist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FakeUserDeviceListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.business.entities.UserDeviceForListing;
import de.njsm.stocks.client.navigation.UserDeviceListNavigator;
import de.njsm.stocks.client.testdata.UserDevicesForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.client.Matchers.equalBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserDeviceListFragmentTest {

    private FragmentScenario<UserDeviceListFragment> scenario;

    private FakeUserDeviceListInteractor userDeviceListInteractor;

    private UserDeviceListNavigator userDeviceListNavigator;

    private EntityDeleter<UserDevice> deleter;

    private Synchroniser synchroniser;

    private Id<User> userId;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        userId = () -> 42;
        when(userDeviceListNavigator.getUserId(any())).thenReturn(userId);
        scenario = FragmentScenario.launchInContainer(UserDeviceListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(synchroniser);
        reset(userDeviceListNavigator);
    }

    @Test
    public void dataIsListed() {
        userDeviceListInteractor.setData(UserDevicesForListing.generate());

        for (UserDeviceForListing item : UserDevicesForListing.generate().devices()) {
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withText(item.name()))));
        }
    }

    @Test
    public void addingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(userDeviceListNavigator).add(equalBy(userId));
    }

    @Test
    public void rightSwipingDeletesItem() {
        var data = UserDevicesForListing.generate();
        userDeviceListInteractor.setData(data);
        int itemIndex = 0;

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, swipeRight()));

        verify(deleter).delete(data.devices().get(itemIndex));
    }

    @Inject
    void setUserDeviceListInteractor(FakeUserDeviceListInteractor userDeviceListInteractor) {
        this.userDeviceListInteractor = userDeviceListInteractor;
    }

    @Inject
    void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    void setUserDeviceListNavigator(UserDeviceListNavigator userDeviceListNavigator) {
        this.userDeviceListNavigator = userDeviceListNavigator;
    }

    @Inject
    void setDeleter(EntityDeleter<UserDevice> deleter) {
        this.deleter = deleter;
    }
}
