package de.njsm.stocks.android.frontend.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.repo.UserRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class SingleUserViewModel extends ViewModel {

    private static final Logger LOG = new Logger(SingleUserViewModel.class);

    private LiveData<User> user;

    private UserRepository userRepo;

    @Inject
    public SingleUserViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int id) {
        if (user == null) {
            LOG.d("Initialising");
            user = userRepo.getUser(id);
        }
    }

    LiveData<User> getUser() {
        return user;
    }

}
