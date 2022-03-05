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

package de.njsm.stocks.client.background.setup;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dagger.android.AndroidInjection;
import de.njsm.stocks.client.execution.Scheduler;
import de.njsm.stocks.client.business.SetupRunner;
import de.njsm.stocks.client.business.entities.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SetupService extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(SetupService.class);

    private SetupRunner setupRunner;

    private Scheduler scheduler;

    @Override
    public void onCreate() {
        LOG.debug("setup service created");
        AndroidInjection.inject(this);

        scheduler.schedule(Job.create(Job.Type.SETUP, () -> {
            setupRunner.setup();
            stopSelf();
        }));
    }

    @Override
    public void onDestroy() {
        LOG.debug("Setup service destroyed");
        setupRunner.giveUpRetrying();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Inject
    public void setSetupRunner(SetupRunner setupRunner) {
        this.setupRunner = setupRunner;
    }

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
