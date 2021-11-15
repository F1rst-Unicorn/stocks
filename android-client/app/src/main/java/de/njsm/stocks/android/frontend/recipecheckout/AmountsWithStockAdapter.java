package de.njsm.stocks.android.frontend.recipecheckout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.db.views.ScaledAmount;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AmountsWithStockAdapter extends AmountsAdapter {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_amount_incrementor, viewGroup, false);
        ViewHolder result = new ViewHolder(v);
        v.setTag(result);
        v.findViewById(R.id.item_amount_incrementor_minus).setOnClickListener(this::decrement);
        v.findViewById(R.id.item_amount_incrementor_plus).setOnClickListener(this::increment);
        return result;
    }

    private void increment(View view) {
        ViewHolder holder = (ViewHolder) ((ConstraintLayout) view.getParent()).getTag();
        int position = holder.getBindingAdapterPosition();
        int valueToModify = currentDistribution.get(position);

        if (data != null && valueToModify < data.getCurrentStock().get(position).getAmount()) {
            currentDistribution.set(position, valueToModify + 1);
            notifyItemChanged(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AmountsAdapter.ViewHolder inputViewHolder, int i) {
        if (data == null)
            return;

        ViewHolder viewHolder = (ViewHolder) inputViewHolder;

        ScaledAmount amount = data.getCurrentStock().get(i);
        int distribution = currentDistribution.get(i);

        viewHolder.setDistribution(amount.getScaledUnit().getScale().multiply(BigDecimal.valueOf(distribution)));
        viewHolder.setInStock(amount.getTotalAmount());
        viewHolder.setUnit(amount.getUnit().getAbbreviation());
    }

    public void setData(@Nullable RecipeFoodCheckout data) {
        if (data != null) {
            currentDistribution = new ArrayList<>(data.getDistribution());
        } else
            currentDistribution = new ArrayList<>();

        this.data = data;
        notifyDataSetChanged();
    }

    protected static final class ViewHolder extends AmountsAdapter.ViewHolder {

        private final TextView inStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.inStock = itemView.findViewById(R.id.item_amount_incrementor_max_counter);
        }

        public void setInStock(BigDecimal totalAmount) {
            inStock.setText(totalAmount.stripTrailingZeros().toPlainString());
        }
    }
}
