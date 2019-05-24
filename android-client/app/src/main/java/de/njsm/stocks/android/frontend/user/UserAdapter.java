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
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class UserAdapter extends BaseAdapter<User, UserAdapter.ViewHolder> {

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
        super(data, onClickListener);
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
    protected void bindConcrete(ViewHolder holder, User data) {
        holder.setText(data.name);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
    }
}
