package de.njsm.stocks.android.frontend.eannumber;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.EanNumberRepository;

import java.util.List;

public class EanNumberViewModel extends ViewModel {

    private LiveData<List<EanNumber>> data;

    private EanNumberRepository repository;

    public EanNumberViewModel(EanNumberRepository repository) {
        this.repository = repository;
    }

    public void init(int foodId) {
        if (data == null) {
            data = repository.getEanCodesOf(foodId);
        }
    }

    public LiveData<List<EanNumber>> getData() {
        return data;
    }

    public LiveData<StatusCode> addEanNumber(String code, int identifies) {
        return repository.addEanCode(code, identifies);
    }

    public LiveData<StatusCode> deleteEanNumber(EanNumber number) {
        return repository.deleteEanCode(number);
    }
}
