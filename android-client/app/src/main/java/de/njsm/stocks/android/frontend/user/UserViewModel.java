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
