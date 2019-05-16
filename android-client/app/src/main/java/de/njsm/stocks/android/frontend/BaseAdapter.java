package de.njsm.stocks.android.frontend;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private LiveData<List<T>> data;

    private Consumer<View> onClickListener;

    public BaseAdapter(LiveData<List<T>> data,
                       Consumer<View> onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    protected void onClick(View v) {
        onClickListener.accept(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        List<T> list = data.getValue();
        if (list != null) {
            T item = list.get(position);
            bindConcrete(holder, item);
        } else {
            bindVoid(holder);
        }
    }

    protected abstract void bindConcrete(VH holder, T data);

    protected abstract void bindVoid(VH holder);

    @Override
    public int getItemCount() {
        List<T> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
