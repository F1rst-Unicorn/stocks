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

package de.njsm.stocks.client.fragment.errorlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.ListDiffer.byId;

public class ErrorDescriptionAdapter extends RecyclerView.Adapter<ErrorDescriptionViewHolder> {

    private List<ErrorDescription> errorDescriptions;

    private final View.OnClickListener onClickListener;

    private final StatusCodeTranslator statusCodeTranslator;

    private final ErrorDetailsHeadlineVisitor errorDetailsHeadlineVisitor;

    private final ErrorDetailsDetailsVisitor errorDetailsDetailsVisitor;

    public ErrorDescriptionAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        statusCodeTranslator = new StatusCodeTranslator();
        errorDetailsHeadlineVisitor = new ErrorDetailsHeadlineVisitor();
        errorDetailsDetailsVisitor = new ErrorDetailsDetailsVisitor();
    }

    public void setData(List<ErrorDescription> newList) {
        List<ErrorDescription> oldList = errorDescriptions;
        errorDescriptions = newList;
        DiffUtil.calculateDiff(byId(oldList, newList, ErrorDescription::id), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ErrorDescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_error, parent, false);
        v.setOnClickListener(onClickListener);
        return new ErrorDescriptionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ErrorDescriptionViewHolder holder, int position) {
        ErrorDescription item = errorDescriptions.get(position);
        holder.setErrorCode(statusCodeTranslator.visit(item.statusCode(), null));
        holder.setHeadline(errorDetailsHeadlineVisitor.visit(item.errorDetails(), null));
        holder.setDetails(errorDetailsDetailsVisitor.visit(item.errorDetails(), null));
    }

    @Override
    public int getItemCount() {
        if (errorDescriptions == null) {
            return 0;
        } else {
            return errorDescriptions.size();
        }
    }
}
