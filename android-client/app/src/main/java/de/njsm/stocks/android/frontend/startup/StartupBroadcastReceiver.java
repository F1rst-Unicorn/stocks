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

package de.njsm.stocks.android.frontend.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.njsm.stocks.android.service.SetupHandler;
import de.njsm.stocks.android.util.Logger;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(StartupBroadcastReceiver.class);

    private StartupFragment startupFragment;

    public StartupBroadcastReceiver(StartupFragment startupFragment) {
        this.startupFragment = startupFragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        if (intent.getAction() == null) return;

        switch (intent.getAction()) {

            case SetupHandler.ACTION_UPDATE:
                startupFragment.setDialogMessage(intent.getIntExtra(SetupHandler.PARAM_MESSAGE, 0));
                break;

            case SetupHandler.ACTION_DONE:
                startupFragment.showTerminationDialog(
                        intent.getIntExtra(SetupHandler.PARAM_TITLE, 0),
                        intent.getIntExtra(SetupHandler.PARAM_MESSAGE, 0),
                        intent.getBooleanExtra(SetupHandler.PARAM_SUCCESS, false)
                );
                break;
        }
    }
}
