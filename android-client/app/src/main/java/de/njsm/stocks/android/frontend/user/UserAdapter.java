package de.njsm.stocks.android.frontend.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private LiveData<List<User>> data;

    private Consumer<View> onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView view;

        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            view = itemView;
        }

        public void setText(CharSequence c) {
            view.setText(c);
        }
    }

    UserAdapter(LiveData<List<User>> data, Consumer<View> onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_user, viewGroup, false);
        ViewHolder result =  new UserAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<User> list = data.getValue();
        if (list != null) {
            String username = list.get(i).name;
            viewHolder.setText(username);
        } else {
            viewHolder.setText("");
        }
    }

    private void onClick(View view) {
        onClickListener.accept(view);
    }

    @Override
    public int getItemCount() {
        List<User> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
