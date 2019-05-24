package de.njsm.stocks.android.frontend.eannumber;

import android.view.View;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.frontend.StringListAdapter;

import java.util.List;

public class EanNumberAdapter extends StringListAdapter<EanNumber> {

    EanNumberAdapter(LiveData<List<EanNumber>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @Override
    protected void bindConcrete(ViewHolder holder, EanNumber data) {
        holder.setText(data.eanCode);
    }
}
