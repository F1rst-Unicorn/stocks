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

package de.njsm.stocks.android.frontend.crashlog;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.InjectedFragment;

import java.util.List;

public class CrashLogListFragment extends InjectedFragment {

    private RecyclerView list;

    private TextView emptyListText;

    private ProgressBar progressBar;

    private CrashLogViewModel viewModel;

    private CrashLogAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_crash_logs, container, false);

        list = result.findViewById(R.id.fragment_crash_logs_list);
        emptyListText = result.findViewById(R.id.fragment_crash_logs_empty_text);
        progressBar = result.findViewById(R.id.fragment_crash_logs_progress);

        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CrashLogViewModel.class);
        addSwipeToDelete(list, viewModel.getData(), viewModel::delete);

        adapter = new CrashLogAdapter(viewModel.getData(), this::onListItemClick);
        viewModel.getData().observe(this, u -> adapter.notifyDataSetChanged());
        viewModel.getData().observe(this, this::maybeShowList);
        list.setAdapter(adapter);
        return result;
    }

    private void maybeShowList(List<CrashLog> crashLogs) {
        if (crashLogs == null) {
            progressBar.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
            emptyListText.setVisibility(View.GONE);
        } else {
            if (crashLogs.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                emptyListText.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                emptyListText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getData().removeObservers(this);
    }

    private void onListItemClick(View v) {
        CrashLogAdapter.ViewHolder holder = (CrashLogAdapter.ViewHolder) v.getTag();
        List<CrashLog> data = viewModel.getData().getValue();
        if (data != null) {
            CrashLog item = data.get(holder.getAdapterPosition());
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, item.getContent());
            i.setType("text/plain");
            startActivity(i);
        }
    }
}
