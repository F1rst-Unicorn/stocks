package de.njsm.stocks.android.frontend.allfood;

import android.view.View;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.StringListAdapter;

import java.util.List;

public class FoodAdapter extends StringListAdapter<Food> {

    public FoodAdapter(LiveData<List<Food>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @Override
    protected void bindConcrete(ViewHolder holder, Food data) {
        holder.setText(data.name);
    }
}
