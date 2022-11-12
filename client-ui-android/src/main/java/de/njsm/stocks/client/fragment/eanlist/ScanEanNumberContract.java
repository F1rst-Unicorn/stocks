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

package de.njsm.stocks.client.fragment.eanlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.activity.result.contract.ActivityResultContract;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ScanEanNumberContract extends ActivityResultContract<Activity, Optional<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(ScanEanNumberContract.class);

    @NotNull
    @Override
    public Intent createIntent(@NotNull Context context, Activity activity) {
        LOG.info("Starting EAN number scanner");
        IntentIntegrator integrator = new IntentIntegrator(activity);
        return integrator.createScanIntent();
    }

    @Override
    public Optional<String> parseResult(int resultCode, @Nullable Intent intent) {
        return Optional.ofNullable(IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, intent))
                .map(IntentResult::getContents)
                .map(v -> {
                    LOG.info("Scanned EAN number: " + v);
                    return v;
                });
    }
}
