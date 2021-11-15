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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmountsAdapter extends RecyclerView.Adapter<AmountsAdapter.ViewHolder> {

    @Nullable
    RecipeFoodCheckout data;

    List<Integer> currentDistribution = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_amount_incrementor_product, viewGroup, false);
        ViewHolder result = new ViewHolder(v);
        v.setTag(result);
        v.findViewById(R.id.item_amount_incrementor_minus).setOnClickListener(this::decrement);
        v.findViewById(R.id.item_amount_incrementor_plus).setOnClickListener(this::increment);
        return result;
    }

    void decrement(View view) {
        ViewHolder holder = (ViewHolder) ((ConstraintLayout) view.getParent()).getTag();
        int valueToModify = currentDistribution.get(holder.getBindingAdapterPosition());

        if (valueToModify > 0) {
            currentDistribution.set(holder.getBindingAdapterPosition(), valueToModify - 1);
            notifyItemChanged(holder.getBindingAdapterPosition());
        }
    }

    private void increment(View view) {
        ViewHolder holder = (ViewHolder) ((ConstraintLayout) view.getParent()).getTag();
        int position = holder.getBindingAdapterPosition();
        int valueToModify = currentDistribution.get(position);
        currentDistribution.set(position, valueToModify + 1);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (data == null)
            return;

        int distribution = currentDistribution.get(i);
        viewHolder.setDistribution(data.getScaledFood().getScaledUnit().getScale().multiply(BigDecimal.valueOf(distribution)));
        viewHolder.setUnit(data.getScaledFood().getUnit().getAbbreviation());
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;

        return data.getCurrentStock().size();
    }

    public void setData(@Nullable RecipeFoodCheckout data) {
        if (data != null) {
            currentDistribution = new ArrayList<>();
            currentDistribution.add(data.getScaledFood().getAmount());
        } else
            currentDistribution = new ArrayList<>();

        this.data = data;
        notifyDataSetChanged();
    }

    public void clear() {
        setData(null);
    }

    public List<Adapter.FormDataItem> getCurrentDistribution() {
        List<Adapter.FormDataItem> result = new ArrayList<>();
        if (data == null)
            return result;


        for (int i = 0; i < currentDistribution.size(); i++) {
            result.add(new Adapter.FormDataItem(
                    data.getScaledFood().getFood().getId(),
                    data.getCurrentStock().get(i).getScaledUnit().getId(),
                    currentDistribution.get(i))
            );
        }
        return result;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView distribution;

        private final TextView unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.distribution = itemView.findViewById(R.id.item_amount_incrementor_stock_counter);
            this.unit = itemView.findViewById(R.id.item_amount_incrementor_unit);
        }

        public void setDistribution(BigDecimal distribution) {
            this.distribution.setText(distribution.stripTrailingZeros().toPlainString());
        }

        public void setUnit(String abbreviation) {
            unit.setText(abbreviation);
        }
    }
}
