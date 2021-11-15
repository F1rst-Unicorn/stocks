package de.njsm.stocks.android.frontend.recipecheckout;

import android.view.View;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.frontend.util.ResourceProvider;
import de.njsm.stocks.common.api.FoodForSetToBuy;

import java.util.List;

public class ProductAdapter extends Adapter {

    public ProductAdapter(ResourceProvider resourceProvider, LiveData<List<RecipeFoodCheckout>> data, Consumer<View> onClickListener, Consumer<FoodForSetToBuy> shoppingCartCallback) {
        super(resourceProvider, data, onClickListener, shoppingCartCallback);
    }

    @Override
    AmountsAdapter buildAdapter() {
        return new AmountsAdapter();
    }
}
