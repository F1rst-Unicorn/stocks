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

import android.view.View;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.presenter.DateRenderStrategy;

import java.time.ZoneId;

public class FoodAdapter extends AbstractFoodAdapter<Food, FoodForListing> {

    protected final DateRenderStrategy dateRenderStrategy;

    private final Clock clock;

    private final ExpirationIconProvider expirationIconProvider;

    public FoodAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, ExpirationIconProvider expirationIconProvider, Clock clock) {
        super(onClickListener, onLongClickListener);
        this.clock = clock;
        this.dateRenderStrategy = new DateRenderStrategy();
        this.expirationIconProvider = expirationIconProvider;
    }

    @Override
    protected void onBindViewHolder(FoodOutlineViewHolder holder, FoodForListing item) {
        holder.setName(item.name());
        holder.showToBuy(item.toBuy());
        holder.setAmount(unitAmountRenderStrategy.render(item.storedAmounts()));
        holder.setExpirationDate(dateRenderStrategy.renderRelative(item.nextEatByDate(), clock.get()));
        holder.setExpirationWarningLevel(expirationIconProvider.computeIcon(item.nextEatByDate().atStartOfDay(ZoneId.systemDefault()).toInstant(), clock.get()));
    }
}
