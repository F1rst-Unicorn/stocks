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

package de.njsm.stocks.android.frontend.main;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.IntFunction;

import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.util.Config;

import static android.view.View.*;

public class EventAdapter extends PagedListAdapter<EntityEvent<?>, RecyclerView.ViewHolder> {

    private final IntFunction<String> stringResourceProvider;

    private final OnClickListener goToEatSoon;

    private final OnClickListener goToEmptyFood;

    private Resources resources;

    private Resources.Theme theme;

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;

        private TextView description;

        private ImageView entityIcon;

        private ImageView eventIcon;

        ViewHolder(@NonNull RelativeLayout itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_event_date);
            description = itemView.findViewById(R.id.item_event_text);
            entityIcon = itemView.findViewById(R.id.item_event_entity_icon);
            eventIcon = itemView.findViewById(R.id.item_event_event_icon);
        }

        public void setDate(CharSequence c) {
            date.setText(c);
        }

        public void setDescription(CharSequence c) {
            description.setText(c);
        }

        public void setEntityIcon(Drawable icon) {
            this.entityIcon.setImageDrawable(icon);
        }

        public void setEventIcon(Drawable icon) {
            this.eventIcon.setImageDrawable(icon);
        }
    }

    EventAdapter(Resources resources,
                 Resources.Theme theme,
                 IntFunction<String> stringResourceProvider,
                 OnClickListener goToEatSoon,
                 OnClickListener goToEmptyFood) {
        super(DIFF_CALLBACK);
        this.resources = resources;
        this.theme = theme;
        this.stringResourceProvider = stringResourceProvider;
        this.goToEatSoon = goToEatSoon;
        this.goToEmptyFood = goToEmptyFood;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == TYPE_HEADER) {
            View header = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_outline_header, viewGroup, false);
            header.findViewById(R.id.fragment_outline_header_cardview).setOnClickListener(goToEatSoon);
            header.findViewById(R.id.fragment_outline_header_cardview2).setOnClickListener(goToEmptyFood);
            return new ViewHolderHeader(header);
        } else {
            RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_event, viewGroup, false);
            ViewHolder result = new EventAdapter.ViewHolder(v);
            v.setTag(result);
            return result;
        }
    }

    @Nullable
    @Override
    protected EntityEvent<?> getItem(int position) {
        if (position == 0)
            return null;
        return super.getItem(position - 1);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0)
            return;

        ViewHolder vh = (ViewHolder) holder;
        EntityEvent<?> event = getItem(position);
        if (event != null) {
            bindConcrete(vh, event);
        } else {
            bindVoid(vh);
        }
    }

    protected void bindConcrete(ViewHolder holder, EntityEvent<?> data) {
        holder.setDescription(data.describe(stringResourceProvider));
        holder.setEventIcon(computeIcon(data.getEventIconResource()));
        holder.setDate(Config.PRETTY_FORMAT.format(data.getTime()));
        holder.setEntityIcon(computeIcon(data.getEntityIconResource()));
    }

    private static final DiffUtil.ItemCallback<EntityEvent<?>> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<EntityEvent<?>>() {
                @Override
                public boolean areItemsTheSame(@NonNull EntityEvent<?> oldItem, @NonNull EntityEvent<?> newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull EntityEvent<?> oldItem, @NonNull EntityEvent<?> newItem) {
                    return true;
                }
            };

    protected void bindVoid(ViewHolder holder) {
        holder.setDescription("");
        holder.setDate("");
        holder.setEventIcon(computeIcon(R.drawable.ic_menu_recent_history_24dp));
        holder.setEntityIcon(computeIcon(R.drawable.ic_menu_recent_history_24dp));
    }

    public Drawable computeIcon(int resId) {
        int black = resources.getColor(android.R.color.black, theme);
        Drawable result = ResourcesCompat.getDrawable(resources, resId, theme);
        assert result != null;

        result.setColorFilter(new PorterDuffColorFilter(
                black,
                PorterDuff.Mode.SRC_ATOP));
        return result;
    }
}
