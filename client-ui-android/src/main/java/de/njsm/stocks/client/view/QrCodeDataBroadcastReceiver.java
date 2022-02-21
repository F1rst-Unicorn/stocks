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

package de.njsm.stocks.client.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QrCodeDataBroadcastReceiver extends BroadcastReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(QrCodeDataBroadcastReceiver.class);

    public static final String ACTION_QR_CODE_SCANNED = QrCodeDataBroadcastReceiver.class.getName() + ".ACTION_QR_CODE_SCANNED";

    public static final String PARAM_QR_CONTENT = QrCodeDataBroadcastReceiver.class.getName() + ".PARAM_QR_CONTENT";

    private final SetupGreetingFragment fragment;

    public QrCodeDataBroadcastReceiver(SetupGreetingFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.debug("Received action " + intent.getAction());
        fragment.getQrResult(intent.getStringExtra(PARAM_QR_CONTENT));
    }
}
