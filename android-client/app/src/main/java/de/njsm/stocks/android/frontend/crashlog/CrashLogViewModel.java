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

package de.njsm.stocks.android.frontend.crashlog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.repo.CrashLogRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class CrashLogViewModel extends ViewModel {

    private static final Logger LOG = new Logger(CrashLogViewModel.class);

    private LiveData<List<CrashLog>> data;

    private CrashLogRepository crashLogRepository;

    @Inject
    public CrashLogViewModel(CrashLogRepository crashLogRepository) {
        this.crashLogRepository = crashLogRepository;
    }

    public void init() {
        if (data == null) {
            LOG.d("initialising");
            data = crashLogRepository.getCrashLogs();
        }
    }

    public LiveData<List<CrashLog>> getData() {
        return data;
    }

    public void delete(CrashLog t) {
        crashLogRepository.delete(t);
    }
}
