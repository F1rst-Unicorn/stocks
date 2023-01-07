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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.CrashLog;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InMemoryCrashListInteractor implements CrashListInteractor {

    private final BehaviorSubject<List<CrashLog>> crashLogs;

    @Inject
    InMemoryCrashListInteractor() {
        List<CrashLog> list = new ArrayList<>();
        list.add(CrashLog.create("crashlog_123.txt", LocalDateTime.now(), RuntimeException.class.getName(), "stacktrace"));
        list.add(CrashLog.create("crashlog_123.txt", LocalDateTime.now(), IOException.class.getName(), "stacktrace"));
        crashLogs = BehaviorSubject.createDefault(list);
    }

    @Override
    public Observable<List<CrashLog>> get() {
        return crashLogs;
    }

    @Override
    public void delete(CrashLog crashLog) {
        crashLogs.firstElement()
                .subscribe(list -> {
                    list.remove(crashLog);
                    crashLogs.onNext(list);
                });
    }
}
