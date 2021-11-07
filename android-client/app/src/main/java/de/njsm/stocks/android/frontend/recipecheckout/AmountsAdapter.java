package de.njsm.stocks.android.frontend.recipecheckout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.db.views.ScaledAmount;

import java.math.BigDecimal;

public class AmountsAdapter extends RecyclerView.Adapter<AmountsAdapter.ViewHolder> {

    @Nullable
    private RecipeFoodCheckout data;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_amount_incrementor, viewGroup, false);
        ViewHolder result = new ViewHolder(v);
        v.setTag(result);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (data == null)
            return;

        ScaledAmount amount = data.getCurrentStock().get(i);
        int distribution = data.getDistribution().get(i);

        viewHolder.setDistribution(amount.getScaledUnit().getScale().multiply(BigDecimal.valueOf(distribution)));
        viewHolder.setInStock(amount.getTotalAmount());
        viewHolder.setUnit(amount.getUnit().getAbbreviation());
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;

        return data.getCurrentStock().size();
    }

    public void setData(RecipeFoodCheckout data) {
        this.data = data;
    }

    public void clear() {
        setData(null);
    }

    protected static final class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView distribution;

        private final TextView inStock;

        private final TextView unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.distribution = itemView.findViewById(R.id.item_amount_incrementor_stock_counter);
            this.inStock = itemView.findViewById(R.id.item_amount_incrementor_max_counter);
            this.unit = itemView.findViewById(R.id.item_amount_incrementor_unit);
        }

        public void setDistribution(BigDecimal distribution) {
            this.distribution.setText(distribution.stripTrailingZeros().toPlainString());
        }

        public void setInStock(BigDecimal totalAmount) {
            inStock.setText(totalAmount.stripTrailingZeros().toPlainString());
        }

        public void setUnit(String abbreviation) {
            unit.setText(abbreviation);
        }
    }
}
