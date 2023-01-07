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

package de.njsm.stocks.client.fragment.userlist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FakeUserListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserForListing;
import de.njsm.stocks.client.navigation.UserListNavigator;
import de.njsm.stocks.client.testdata.UsersForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class UserListFragmentTest {

    private FragmentScenario<UserListFragment> scenario;

    private FakeUserListInteractor userListInteractor;

    private EntityDeleter<User> deleter;

    private UserListNavigator userListNavigator;

    private Synchroniser synchroniser;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(UserListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(synchroniser);
        reset(userListNavigator);
    }

    @Test
    public void dataIsListed() {
        userListInteractor.setData(UsersForListing.generate());

        for (UserForListing item : UsersForListing.generate()) {
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withText(item.name()))));
        }
    }

    @Test
    public void clickingNavigates() {
        int itemIndex = 1;
        List<UserForListing> data = UsersForListing.generate();
        assertTrue("The test wants to access element " + itemIndex, data.size() >= itemIndex + 1);
        UserForListing user = data.get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", user.id() != itemIndex);
        userListInteractor.setData(data);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, click()));

        verify(userListNavigator).show(user.id());
    }

    @Test
    public void addingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(userListNavigator).add();
    }

    @Test
    public void rightSwipingDeletesItem() {
        var data = UsersForListing.generate();
        userListInteractor.setData(data);
        int itemIndex = 0;

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, swipeRight()));

        verify(deleter).delete(data.get(itemIndex));
    }

    @Inject
    void setUserListInteractor(FakeUserListInteractor userListInteractor) {
        this.userListInteractor = userListInteractor;
    }

    @Inject
    void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    void setUserListNavigator(UserListNavigator userListNavigator) {
        this.userListNavigator = userListNavigator;
    }

    @Inject
    void setDeleter(EntityDeleter<User> deleter) {
        this.deleter = deleter;
    }
}
