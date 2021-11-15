package de.njsm.stocks.android.frontend.recipecheckout;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.frontend.BaseAdapter;
import de.njsm.stocks.android.frontend.util.ResourceProvider;
import de.njsm.stocks.common.api.FoodForSetToBuy;

import java.util.*;

public abstract class Adapter extends BaseAdapter<RecipeFoodCheckout, Adapter.ViewHolder> {

    private final ResourceProvider resourceProvider;

    private final Consumer<FoodForSetToBuy> shoppingCartCallback;

    private final Map<ViewHolder, AmountsAdapter> amountsWithStockAdapters;

    public Adapter(ResourceProvider resourceProvider, LiveData<List<RecipeFoodCheckout>> data, Consumer<View> onClickListener, Consumer<FoodForSetToBuy> shoppingCartCallback) {
        super(data, onClickListener);
        this.resourceProvider = resourceProvider;
        this.shoppingCartCallback = shoppingCartCallback;
        amountsWithStockAdapters = new HashMap<>();
    }

    @Override
    protected void bindConcrete(ViewHolder holder, RecipeFoodCheckout data) {
        holder.setFoodName(data.getScaledFood().getFood().getName());
        holder.setRequiredAmount(data.getScaledFood().getPrettyUnitName());
        holder.setShoppingIcon(data.isToBuy());
        holder.setAmounts(data);
        amountsWithStockAdapters.put(holder, holder.getAdapter());
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setFoodName("");
        holder.setRequiredAmount("");
        holder.setShoppingIcon(false);
        holder.clearAmounts();
        amountsWithStockAdapters.remove(holder);
    }

    public List<FormDataItem> collectData() {
        List<FormDataItem> result = new ArrayList<>();

        for (AmountsAdapter adapter : amountsWithStockAdapters.values()) {
            result.addAll(adapter.getCurrentDistribution());
        }

        return result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recipe_item, viewGroup, false);
        ViewHolder result = new ViewHolder(v);

        RecyclerView amounts = v.findViewById(R.id.item_recipe_item_amounts);
        amounts.setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        amounts.setAdapter(buildAdapter());

        v.setTag(result);
        v.findViewById(R.id.item_recipe_item_shopping_cart).setOnClickListener(this::onShoppingCartClicked);
        return result;
    }

    abstract AmountsAdapter buildAdapter();

    private void onShoppingCartClicked(View view) {
        ViewHolder holder = (ViewHolder) ((CardView) view.getParent().getParent()).getTag();
        List<RecipeFoodCheckout> list = getData().getValue();
        if (list == null)
            return;

        RecipeFoodCheckout dataItem = list.get(holder.getBindingAdapterPosition());

        FoodForSetToBuy apiData = FoodForSetToBuy.builder()
                .id(dataItem.getScaledFood().getFood().getId())
                .version(dataItem.getScaledFood().getFood().getVersion())
                .toBuy(!dataItem.isToBuy())
                .build();

        shoppingCartCallback.accept(apiData);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView foodName;

        private final TextView requiredAmount;

        private final ImageButton shoppingIcon;

        private final RecyclerView amounts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.item_recipe_item_food_name);
            requiredAmount = itemView.findViewById(R.id.item_recipe_item_scaled_unit);
            shoppingIcon = itemView.findViewById(R.id.item_recipe_item_shopping_cart);
            amounts = itemView.findViewById(R.id.item_recipe_item_amounts);
        }

        private void setFoodName(CharSequence name) {
            foodName.setText(name);
        }

        public void setRequiredAmount(String prettyUnitName) {
            requiredAmount.setText(prettyUnitName);
        }

        public void setShoppingIcon(boolean toBuy) {
            Drawable icon;

            if (toBuy) {
                icon = resourceProvider.getDrawable(R.drawable.baseline_remove_shopping_cart_black_24);
            } else {
                icon = resourceProvider.getDrawable(R.drawable.baseline_add_shopping_cart_black_24);
            }

            shoppingIcon.setImageDrawable(icon);
        }

        public AmountsAdapter getAdapter() {
            return (AmountsAdapter) amounts.getAdapter();
        }

        public void setAmounts(RecipeFoodCheckout data) {
            ((AmountsAdapter) Objects.requireNonNull(amounts.getAdapter())).setData(data);
        }

        public void clearAmounts() {
            ((AmountsAdapter) Objects.requireNonNull(amounts.getAdapter())).clear();
        }
    }

    public static class FormDataItem {

        private final int foodId;

        private final int scaledUnitId;

        private final int amount;

        public FormDataItem(int foodId, int scaledUnitId, int amount) {
            this.foodId = foodId;
            this.scaledUnitId = scaledUnitId;
            this.amount = amount;
        }

        public int getFoodId() {
            return foodId;
        }

        public int getScaledUnitId() {
            return scaledUnitId;
        }

        public int getAmount() {
            return amount;
        }
    }
}
