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
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.fragment.view.ValueFormatter;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PriceShowView {

    private final TableLayout table;

    private final RadioButton byChainButton;

    private final LineChart lineChart;

    private final List<TableRow> currentRows;

    private final Context context;

    private final Function<Integer, String> dictionary;

    private final DateRenderStrategy dateRenderStrategy;

    private List<PricePlot<GroceryChain, LocalDateTime>> groceryChainPlot;

    private List<PricePlot<GroceryStore, LocalDateTime>> groceryStorePlot;

    private int[] lineColour;

    public PriceShowView(View root, Function<Integer, String> dictionary, DateRenderStrategy dateRenderStrategy) {
        table = root.findViewById(R.id.fragmet_price_show_table);
        context = root.getContext();
        byChainButton = root.findViewById(R.id.fragment_price_show_chart_settings_by_chain);
        lineChart = root.findViewById(R.id.fragment_price_show_chart);
        this.dictionary = dictionary;
        currentRows = new ArrayList<>();
        this.dateRenderStrategy = dateRenderStrategy;

        View templateRow = root.findViewById(R.id.fragment_price_show_data_template_row);
        table.removeView(templateRow);
        byChainButton.setChecked(true);
        byChainButton.setOnCheckedChangeListener((v, value) -> {
            if (value) {
                setDiagramData(groceryChainPlot);
            }
        });
        root.<RadioButton>findViewById(R.id.fragment_price_show_chart_settings_by_store).setOnCheckedChangeListener((v, value) -> {
            if (value) {
                setDiagramData(groceryStorePlot);
            }
        });

        setupChart(dateRenderStrategy);
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
                            unitAmountRenderStrategy.render(price.price().normalisedPrice()),
                            unitAmountRenderStrategy.render(price.price().normalisedQuantity())));
            priceView.setPadding(8, 8, 8, 8);
            priceView.setGravity(Gravity.END);
            row.addView(date);
            row.addView(store);
            row.addView(priceView);
            table.addView(row);
            currentRows.add(row);
        }
    }

    public void setChartData(
            List<PricePlot<GroceryChain, LocalDateTime>> groceryChainPlot,
            List<PricePlot<GroceryStore, LocalDateTime>> groceryStorePlot,
            @ColorInt int[] lineColour) {
        this.groceryChainPlot = groceryChainPlot;
        this.groceryStorePlot = groceryStorePlot;
        this.lineColour = lineColour;

        if (byChainButton.isChecked()) {
            setDiagramData(groceryChainPlot);
        } else {
            setDiagramData(groceryStorePlot);
        }
    }

    private void setupChart(DateRenderStrategy dateRenderStrategy) {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter(dateRenderStrategy));
        lineChart.setDrawBorders(false);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHardwareAccelerationEnabled(true);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setAxisMinimum(0);
        lineChart.getAxisRight().setGranularity(1);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisLeft().setGranularity(1);
        lineChart.getXAxis().setGranularity(86400f);
        lineChart.getXAxis().setGridLineWidth(0.5f);
    }

    private <E extends Entity<E>> void setDiagramData(List<PricePlot<E, LocalDateTime>> plotData) {
        if (plotData == null) {
            return;
        }

        LineData data = new LineData();
        int colourIndex = 0;
        for (var dataSource : plotData) {
            if (dataSource.plotPoints().size() < 2)
                continue;

            var entries = dataSource.plotPoints().stream()
                    .map(v -> new Entry(dateRenderStrategy.toFloat(v.x()), v.y().floatValue()))
                    .collect(Collectors.toList());
            LineDataSet dataSet = new LineDataSet(entries, dataSource.name());
            dataSet.setColor(lineColour[colourIndex]);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.STEPPED);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            data.addDataSet(dataSet);
            colourIndex = (colourIndex + 1) % lineColour.length;
        }
        data.setDrawValues(false);
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
