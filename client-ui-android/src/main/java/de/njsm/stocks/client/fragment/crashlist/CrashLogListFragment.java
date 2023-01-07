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

package de.njsm.stocks.client.fragment.crashlist;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.CrashLog;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.util.CameraPermissionProber;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.presenter.CrashLogListViewModel;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class CrashLogListFragment extends BottomToolbarFragment implements CameraPermissionProber {

    private CrashLogListViewModel crashLogListViewModel;

    private CrashListAdapter adapter;

    private TemplateSwipeList templateSwipeList;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);
        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        adapter = new CrashListAdapter(this::onItemClicked, new DateRenderStrategy(null));
        crashLogListViewModel.get().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), adapter, callback);
        templateSwipeList.disableSwipeRefresh();
        templateSwipeList.hideFloatingActionButton();

        return root;
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((CrashLogViewHolder) listItem.getTag()).getBindingAdapterPosition();
        crashLogListViewModel.resolve(listItemIndex, crashLog -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, crashLog.renderedContent());
            i.putExtra(Intent.EXTRA_EMAIL, new String[] {"stocks" + "@" + "njsm.de"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Stocks Exception Log");
            i.setType("text/plain");
            startActivity(i);
        });
    }

    private void onListDataReceived(List<CrashLog> data) {
        if (data.isEmpty())
            templateSwipeList.setEmpty(R.string.text_no_crash_logs);
        else
            templateSwipeList.setList();
        adapter.setData(data);
    }

    private void onItemSwipedRight(int listItemIndex) {
        crashLogListViewModel.delete(listItemIndex);
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        crashLogListViewModel = viewModelProvider.get(CrashLogListViewModel.class);
    }
}
