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

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.core.content.res.ResourcesCompat;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

public class FoodAdapter extends AbstractFoodAdapter<Food, FoodForListing> {

    protected final DateRenderStrategy dateRenderStrategy;

    private final Clock clock;

    private final IconCache iconCache;

    public FoodAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, Resources resources, Resources.Theme theme, Clock clock) {
        super(onClickListener, onLongClickListener);
        this.clock = clock;
        this.dateRenderStrategy = new DateRenderStrategy();

        int red = resources.getColor(android.R.color.holo_red_light, theme);
        int green = resources.getColor(R.color.colorPrimary, theme);
        iconCache = new IconCache(ResourcesCompat.getDrawable(resources, R.drawable.ic_error_black_24dp, theme),
                ResourcesCompat.getDrawable(resources, R.drawable.ic_check_black_24dp, theme),
                ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm_black_24dp, theme),
                red, green);
    }

    @Override
    protected void onBindViewHolder(FoodOutlineViewHolder holder, FoodForListing item) {
        holder.setName(item.name());
        holder.showToBuy(item.toBuy());
        holder.setAmount(unitAmountRenderStrategy.render(item.storedAmounts()));
        holder.setExpirationDate(dateRenderStrategy.renderRelative(item.nextEatByDate(), clock.get()));
        holder.setExpirationWarningLevel(computeIcon(item.nextEatByDate().atStartOfDay(ZoneId.systemDefault()).toInstant(), clock.get()));
    }

    private Drawable computeIcon(Instant expiration, Instant now) {
        Instant inFiveDays = now.plus(Duration.ofDays(5));
        if (expiration.isBefore(now)) {
            return iconCache.error;
        } else if (expiration.isAfter(inFiveDays)) {
            return iconCache.ok;
        } else {
            return iconCache.warning;
        }
    }

    private static class IconCache {
        private final Drawable error;
        private final Drawable warning;
        private final Drawable ok;

        public IconCache(Drawable error, Drawable ok, Drawable warning, @ColorInt int red, @ColorInt int green) {
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
    }
}
