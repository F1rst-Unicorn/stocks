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

package de.njsm.stocks.android.frontend.eannumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.util.Consumer;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.util.Logger;

public class EanNumberBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(EanNumberBroadcastReceiver.class);

    private Consumer<String> eanNumberCreator;

    public EanNumberBroadcastReceiver(Consumer<String> eanNumberCreator) {
        this.eanNumberCreator = eanNumberCreator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.d("Received action " + intent.getAction());
        eanNumberCreator.accept(intent.getStringExtra(MainActivity.PARAM_QR_CONTENT));
    }
}
