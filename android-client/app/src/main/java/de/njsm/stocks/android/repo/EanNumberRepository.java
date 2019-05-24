package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.EanNumberDao;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class EanNumberRepository {

    private static final Logger LOG = new Logger(EanNumberRepository.class);

    private EanNumberDao eanNumberDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public EanNumberRepository(EanNumberDao eanNumberDao,
                               ServerClient webClient,
                               Synchroniser synchroniser) {
        this.eanNumberDao = eanNumberDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<EanNumber>> getEanCodesOf(int id) {
        LOG.d("getting numbers of id " + id);
        return eanNumberDao.getEanNumbersOf(id);
    }

    public LiveData<StatusCode> addEanCode(String code, int identifies) {
        LOG.d("adding code " + code + " for food " + identifies);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addEanNumber(code, identifies)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteEanCode(EanNumber number) {
        LOG.d("deleting number " + number);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteEanNumber(number.id, number.version)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }
}
