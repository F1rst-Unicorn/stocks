package de.njsm.stocks.android.frontend.crashlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.business.CrashLog;

import java.util.List;

public class CrashLogAdapter extends RecyclerView.Adapter<CrashLogAdapter.ViewHolder> {

    private LiveData<List<CrashLog>> data;

    private Consumer<View> onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private TextView date;

        ViewHolder(@NonNull RelativeLayout layout) {
            super(layout);
            name = layout.findViewById(R.id.item_crash_log_name);
            date = layout.findViewById(R.id.item_crash_log_date);
        }

        public void setName(CharSequence c) {
            name.setText(c);
        }

        public void setDate(CharSequence c) {
            date.setText(c);
        }
    }

    CrashLogAdapter(LiveData<List<CrashLog>> data, Consumer<View> onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_crash_log, viewGroup, false);
        CrashLogAdapter.ViewHolder result = new CrashLogAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    private void onClick(View view) {
        onClickListener.accept(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<CrashLog> list = data.getValue();
        if (list != null) {
            CrashLog item = list.get(i);
            viewHolder.setDate(item.getDate());
            viewHolder.setName(item.getName());
        } else {
            viewHolder.setDate("");
            viewHolder.setName("");
        }
    }

    @Override
    public int getItemCount() {
        List<CrashLog> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
