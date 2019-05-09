package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.business.Principals;
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class UserRepository {

    private static final Logger LOG = new Logger(UserRepository.class);

    private UserDao userDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public UserRepository(UserDao userDao,
                          ServerClient webClient,
                          Synchroniser synchroniser) {
        this.userDao = userDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<User>> getUsers() {
        LOG.d("getting users");
        return userDao.getAll();
    }

    public LiveData<StatusCode> addUser(String name) {
        LOG.d("adding user " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        if (! Principals.isNameValid(name)) {
            data.setValue(StatusCode.INVALID_ARGUMENT);
            return data;
        }

        webClient.addUser(name)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteUser(User entity) {
        LOG.d("deleting user " + entity);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteUser(entity.id, entity.version)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }
}
