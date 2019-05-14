package de.njsm.stocks.android.frontend.crashlog;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.SwipeCallback;

import javax.inject.Inject;
import java.util.List;

public class CrashLogListFragment extends BaseFragment {

    private RecyclerView list;

    private TextView emptyListText;

    private ProgressBar progressBar;

    private ViewModelProvider.Factory viewModelFactory;

    private CrashLogViewModel viewModel;

    private CrashLogAdapter adapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_crash_logs, container, false);

        list = result.findViewById(R.id.crash_logs_list);
        emptyListText = result.findViewById(R.id.crash_logs_empty_text);
        progressBar = result.findViewById(R.id.crash_logs_progress);

        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CrashLogViewModel.class);
        SwipeCallback<CrashLog> callback = new SwipeCallback<>(
                viewModel.getData().getValue(),
                this::deleteCrashLog,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)));
        viewModel.getData().observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);

        adapter = new CrashLogAdapter(viewModel.getData(), this::onListItemClick);
        viewModel.getData().observe(this, u -> adapter.notifyDataSetChanged());
        viewModel.getData().observe(this, this::maybeShowList);
        list.setAdapter(adapter);
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
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

    private void deleteCrashLog(CrashLog t) {
        if (t.getFile() != null) {
            viewModel.delete(t);
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
