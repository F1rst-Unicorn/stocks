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

package de.njsm.stocks.android.frontend.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.UserRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class UserViewModel extends ViewModel {

    private static final Logger LOG = new Logger(UserViewModel.class);

    private LiveData<List<User>> users;

    private UserRepository userRepo;

    @Inject
    public UserViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init() {
        if (users == null) {
            LOG.d("Initialising");
            users = userRepo.getUsers();
        }
    }

    LiveData<List<User>> getUsers() {
        return users;
    }

    LiveData<StatusCode> addUser(String name) {
        return userRepo.addUser(name);
    }

    LiveData<StatusCode> deleteUser(User item) {
        return userRepo.deleteUser(item);
    }
}
