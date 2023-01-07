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
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.execution.Scheduler;
import de.njsm.stocks.client.runtime.FileInteractor;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.njsm.stocks.client.runtime.ExceptionHandler.TECHNICAL_DATE_FORMAT;

@Singleton
class CrashListInteractorImpl implements CrashListInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(CrashListInteractorImpl.class);

    private final FileInteractor fileInteractor;

    private final Scheduler scheduler;

    private final Localiser localiser;

    private final BehaviorSubject<List<CrashLog>> crashLogs;

    @Inject
    CrashListInteractorImpl(FileInteractor fileInteractor, Scheduler scheduler, Localiser localiser) {
        this.fileInteractor = fileInteractor;
        this.scheduler = scheduler;
        this.localiser = localiser;
        this.crashLogs = BehaviorSubject.create();
    }

    @Override
    public Observable<List<CrashLog>> get() {
        scheduler.schedule(Job.create(Job.Type.CRASH_LOG_HANDLING, this::refreshList));
        return crashLogs;
    }

    public void transferTo(InputStreamReader in, Writer out) throws IOException {
        Objects.requireNonNull(out, "out");

        int nRead;
        for(char[] buffer = new char[8192]; (nRead = in.read(buffer, 0, 8192)) >= 0;) {
            out.write(buffer, 0, nRead);
        }
    }

    @Override
    public void delete(CrashLog crashLog) {
        scheduler.schedule(Job.create(Job.Type.CRASH_LOG_HANDLING, () -> {
            fileInteractor.delete(new File(crashLog.fileName()));
            refreshList();
        }));
    }

    private void refreshList() {
        File[] crashLogs = fileInteractor.listCrashLogs();
        List<CrashLog> list = new ArrayList<>();
        for (File file : crashLogs) {
            try (var stream = fileInteractor.getFileInputStream(file)) {
                StringWriter stackTraceWriter = new StringWriter();
                transferTo(new InputStreamReader(stream), stackTraceWriter);
                var content = stackTraceWriter.toString();
                String[] lines = content.split("\n", 3);
                if (lines.length != 3) {
                    LOG.error("invalid format on file " + file);
                    continue;
                }
                list.add(CrashLog.create(
                        file.getName(),
                        localiser.toLocalDateTime(TECHNICAL_DATE_FORMAT.parse(lines[1], Instant::from)),
                        lines[0],
                        lines[2]
                ));
            } catch (IOException e) {
                LOG.error("failed to read " + file, e);
            }
        }
        this.crashLogs.onNext(list);
    }
}
