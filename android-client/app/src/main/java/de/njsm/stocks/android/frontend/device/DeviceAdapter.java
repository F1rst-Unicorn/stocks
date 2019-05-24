package de.njsm.stocks.android.frontend.device;

import android.view.View;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.frontend.StringListAdapter;

import java.util.List;

public class DeviceAdapter extends StringListAdapter<UserDevice> {

    DeviceAdapter(LiveData<List<UserDevice>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @Override
    protected void bindConcrete(ViewHolder holder, UserDevice data) {
        holder.setText(data.name);
    }
}
