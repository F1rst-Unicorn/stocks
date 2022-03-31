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

package de.njsm.stocks.client.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.presenter.ToolbarViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class BottomToolbarFragment extends InjectableFragment {

    private ToolbarViewModel toolbarViewModel;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frame, container, false);

        BottomToolbar bottomToolbar = new BottomToolbar(root);
        toolbarViewModel.backgroundJobCounter().observe(getViewLifecycleOwner(), bottomToolbar::setBackgroundCounter);
        toolbarViewModel.errorCounter().observe(getViewLifecycleOwner(), bottomToolbar::setErrorCounter);

        return root;
    }

    View insertContent(@NonNull LayoutInflater inflater, View root, @LayoutRes int contentLayout) {
        LinearLayout container = root.findViewById(R.id.fragment_frame_content);
        View content = inflater.inflate(contentLayout, container, false);
        container.addView(content);
        return content;
    }

    @Inject
    @CallSuper
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        toolbarViewModel = viewModelProvider.get(ToolbarViewModel.class);
    }
}
