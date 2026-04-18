/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.fragment.priceshow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.PriceForTableListing;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PriceShowView {

    private final TableLayout table;

    private final List<TableRow> currentRows;

    private final Context context;

    private final Function<Integer, String> dictionary;

    public PriceShowView(View root, Function<Integer, String> dictionary) {
        table = root.findViewById(R.id.fragmet_price_show_table);
        context = root.getContext();
        this.dictionary = dictionary;
        currentRows = new ArrayList<>();

        View templateRow = root.findViewById(R.id.fragment_price_show_data_template_row);
        table.removeView(templateRow);
    }

    void setTableData(List<PriceForTableListing> prices, Localiser localiser) {
        DateRenderStrategy dateRenderStrategy = new DateRenderStrategy(localiser);
        UnitAmountRenderStrategy unitAmountRenderStrategy = new UnitAmountRenderStrategy();

        currentRows.forEach(table::removeView);
        currentRows.clear();

        for (var price : prices) {
            TableRow row = new TableRow(context);
            TextView date = new TextView(context);
            date.setText(dateRenderStrategy.render(price.date()));
            date.setPadding(8, 8, 8, 8);
            date.setGravity(Gravity.START);
            TextView store = new TextView(context);
            store.setText(price.groceryStore());
            store.setPadding(8, 8, 8, 8);
            store.setGravity(Gravity.START);
            TextView priceView = new TextView(context);
            priceView.setText(String.format(dictionary.apply(R.string.text_fraction),
                            unitAmountRenderStrategy.render(price.price()),
                            unitAmountRenderStrategy.render(price.quantity())));
            priceView.setPadding(8, 8, 8, 8);
            priceView.setGravity(Gravity.END);
            row.addView(date);
            row.addView(store);
            row.addView(priceView);
            table.addView(row);
            currentRows.add(row);
        }
    }
}
