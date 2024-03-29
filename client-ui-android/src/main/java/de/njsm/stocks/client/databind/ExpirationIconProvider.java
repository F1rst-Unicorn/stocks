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

package de.njsm.stocks.client.databind;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class ExpirationIconProvider {

    private final Drawable error;

    private final Drawable warning;

    private final Drawable ok;

    @Inject
    ExpirationIconProvider(Context context) {
        Resources resources = context.getResources();
        Resources.Theme theme = context.getTheme();
        int red = resources.getColor(android.R.color.holo_red_light, theme);
        int green = resources.getColor(R.color.colorPrimary, theme);

        Drawable error = ResourcesCompat.getDrawable(resources, R.drawable.ic_error_black_24dp, theme);
        Drawable ok = ResourcesCompat.getDrawable(resources, R.drawable.ic_check_black_24dp, theme);
        Drawable warning = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm_black_24dp, theme);
        Objects.requireNonNull(error);
        Objects.requireNonNull(warning);
        Objects.requireNonNull(ok);
        this.error = error.mutate();
        this.warning = warning.mutate();
        this.ok = ok.mutate();

        PorterDuffColorFilter redFilter = new PorterDuffColorFilter(
                red,
                PorterDuff.Mode.SRC_ATOP);
        error.setColorFilter(redFilter);
        warning.setColorFilter(redFilter);
        ok.setColorFilter(new PorterDuffColorFilter(
                green,
                PorterDuff.Mode.SRC_ATOP
        ));
    }

    public Drawable computeIcon(LocalDateTime expiration, LocalDateTime now) {
        LocalDateTime inFiveDays = now.plus(Duration.ofDays(5));
        if (expiration.isBefore(now)) {
            return error;
        } else if (expiration.isAfter(inFiveDays)) {
            return ok;
        } else {
            return warning;
        }
    }
}
