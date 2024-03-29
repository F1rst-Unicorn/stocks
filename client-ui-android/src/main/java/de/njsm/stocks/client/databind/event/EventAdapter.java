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

package de.njsm.stocks.client.databind.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static de.njsm.stocks.client.fragment.util.ItemDiffer.byId;

public class EventAdapter extends PagingDataAdapter<ActivityEvent, EventViewHolder> {

    private final View.OnClickListener onClickListener;

    private final DateRenderStrategy dateRenderStrategy;

    private final DescriptionRenderer descriptionRenderer;

    private final DataIconSelector dataIconSelector;

    private final ActionIconSelector actionIconSelector;

    public EventAdapter(View.OnClickListener onClickListener,
                        Localiser localiser,
                        Function<Integer, String> dictionary) {
        super(byId(ActivityEvent::timeOccurred));
        this.onClickListener = onClickListener;
        dateRenderStrategy = new DateRenderStrategy(localiser);
        descriptionRenderer = new DescriptionRenderer(dictionary, dateRenderStrategy);
        dataIconSelector = new DataIconSelector();
        actionIconSelector = new ActionIconSelector();
    }

    @NonNull
    @NotNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        v.setOnClickListener(onClickListener);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventViewHolder holder, int position) {
        ActivityEvent item = getItem(position);
        if (item == null) {
            holder.setLoading();
        } else {
            holder.setTag(item);
            holder.setTime(dateRenderStrategy.render(item.timeOccurred()));
            holder.setText(descriptionRenderer.visit(item));
            holder.setDataIcon(dataIconSelector.visit(item));
            holder.setActionIcon(actionIconSelector.visit(item));
        }
    }
}
