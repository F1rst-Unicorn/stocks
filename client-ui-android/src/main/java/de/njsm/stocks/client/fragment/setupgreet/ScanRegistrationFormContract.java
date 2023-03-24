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

package de.njsm.stocks.client.fragment.setupgreet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class ScanRegistrationFormContract extends ActivityResultContract<Activity, Optional<RegistrationForm>> {

    private static final Logger LOG = LoggerFactory.getLogger(ScanRegistrationFormContract.class);

    private final ScanContract delegate;

    public ScanRegistrationFormContract() {
        this.delegate = new ScanContract();
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Activity input) {
        LOG.info("Starting QR code scanner");
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        return delegate.createIntent(context, options);
    }

    @Override
    public Optional<RegistrationForm> parseResult(int resultCode, @Nullable Intent intent) {
        return Optional.ofNullable(delegate.parseResult(resultCode, intent))
                .map(ScanIntentResult::getContents)
                .map(v -> {
                    LOG.info("Scanned QR code: " + v);
                    return v;
                })
                .map(RegistrationForm::parseRawString);
    }
}
