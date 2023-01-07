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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.entities.UserForDeletion;
import de.njsm.stocks.client.business.entities.UserForListing;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.testList;
import static org.junit.Assert.assertEquals;

public class UserListRepositoryImplTest extends DbTestCase {

    private UserListRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new UserListRepositoryImpl(stocksDatabase.userDao());
    }

    @Test
    public void gettingUsersWorks() {
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));

        var actual = uut.getUsers();

        testList(actual).assertValue(List.of(UserForListing.create(user.id(), user.name())));
    }

    @Test
    public void gettingUserForDeletionWorks() {
        UserDbEntity user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));

        var actual = uut.getEntityForDeletion(user::id);

        assertEquals(UserForDeletion.create(user.id(), user.version()), actual);
    }
}