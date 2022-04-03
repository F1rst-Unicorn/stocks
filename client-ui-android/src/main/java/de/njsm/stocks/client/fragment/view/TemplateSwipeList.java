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

package de.njsm.stocks.client.fragment.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.njsm.stocks.client.ui.R;

public class TemplateSwipeList {

    private final View root;

    private final RecyclerView list;

    private final View loadingAnimation;

    private final TextView noDataText;

    public TemplateSwipeList(View root) {
        this.root = root;
        list = root.findViewById(R.id.template_swipe_list_list);
        loadingAnimation = root.findViewById(R.id.template_swipe_list_progress_indicator);
        noDataText = root.findViewById(R.id.template_swipe_list_empty_text);
    }

    public void setLoading() {
        list.setVisibility(View.GONE);
        loadingAnimation.setVisibility(View.VISIBLE);
        noDataText.setVisibility(View.GONE);
    }

    public void setEmpty(@StringRes int emptyHint) {
        list.setVisibility(View.GONE);
        loadingAnimation.setVisibility(View.GONE);
        noDataText.setVisibility(View.VISIBLE);
        noDataText.setText(emptyHint);
    }

    public void setList() {
        list.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.GONE);
        noDataText.setVisibility(View.GONE);
    }

    <T extends RecyclerView.ViewHolder> void initialiseList(Context context, RecyclerView.Adapter<T> adapter) {
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(adapter);
    }

    public <T extends RecyclerView.ViewHolder> void initialiseListWithSwiper(Context context, RecyclerView.Adapter<T> adapter, ItemTouchHelper.SimpleCallback swipeCallback) {
        initialiseList(context, adapter);
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(list);
    }

    public void bindFloatingActionButton(View.OnClickListener onClickListener) {
        FloatingActionButton addButton = root.findViewById(R.id.template_swipe_list_fab);
        addButton.setOnClickListener(onClickListener);
    }

    public void hideFloatingActionButton() {
        FloatingActionButton addButton = root.findViewById(R.id.template_swipe_list_fab);
        addButton.setVisibility(View.GONE);
    }

    public void bindSwipeDown(Runnable listener) {
        SwipeRefreshLayout refreshLayout = root.findViewById(R.id.template_swipe_list_swipe);
        refreshLayout.setOnRefreshListener(() -> {
            listener.run();
            refreshLayout.setRefreshing(false);
        });
    }
}
