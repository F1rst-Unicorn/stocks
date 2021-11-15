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

import java.util.List;
import java.util.Objects;

public class Adapter extends BaseAdapter<RecipeFoodCheckout, Adapter.ViewHolder> {

    private final ResourceProvider resourceProvider;

    private final Consumer<FoodForSetToBuy> shoppingCartCallback;

    public Adapter(ResourceProvider resourceProvider, LiveData<List<RecipeFoodCheckout>> data, Consumer<View> onClickListener, Consumer<FoodForSetToBuy> shoppingCartCallback) {
        super(data, onClickListener);
        this.resourceProvider = resourceProvider;
        this.shoppingCartCallback = shoppingCartCallback;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, RecipeFoodCheckout data) {
        holder.setFoodName(data.getScaledFood().getFood().getName());
        holder.setRequiredAmount(data.getScaledFood().getPrettyUnitName());
        holder.setShoppingIcon(data.isToBuy());
        holder.setAmounts(data);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setFoodName("");
        holder.setRequiredAmount("");
        holder.setShoppingIcon(false);
        holder.clearAmounts();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView v = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recipe_item, viewGroup, false);
        ViewHolder result = new ViewHolder(v);

        RecyclerView amounts = v.findViewById(R.id.item_recipe_item_amounts);
        amounts.setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        amounts.setAdapter(new AmountsAdapter());

        v.setTag(result);
        v.findViewById(R.id.item_recipe_item_shopping_cart).setOnClickListener(this::onShoppingCartClicked);
        return result;
    }

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

        public void setAmounts(RecipeFoodCheckout data) {
            ((AmountsAdapter) Objects.requireNonNull(amounts.getAdapter())).setData(data);
            amounts.getAdapter().notifyDataSetChanged();
        }

        public void clearAmounts() {
            ((AmountsAdapter) Objects.requireNonNull(amounts.getAdapter())).clear();
        }
    }
}
