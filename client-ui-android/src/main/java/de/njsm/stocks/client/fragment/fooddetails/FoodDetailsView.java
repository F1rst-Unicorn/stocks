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

package de.njsm.stocks.client.fragment.fooddetails;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import de.njsm.stocks.client.business.entities.PlotByUnit;
import de.njsm.stocks.client.business.entities.PlotPoint;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class FoodDetailsView {

    private final TextView amounts;

    private final TextView location;

    private final TextView expirationOffset;

    private final TextView storeUnit;

    private final UnitAmountRenderStrategy strategy;

    private final DateRenderStrategy dateRenderStrategy;

    private final LineChart lineChart;

    private final BarChart barChart;

    @Inject
    FoodDetailsView(View root, DateRenderStrategy dateRenderStrategy) {
        this.dateRenderStrategy = dateRenderStrategy;
        amounts = root.findViewById(R.id.fragment_food_details_amount);
        location = root.findViewById(R.id.fragment_food_details_location);
        expirationOffset = root.findViewById(R.id.fragment_food_details_expiration_offset);
        storeUnit = root.findViewById(R.id.fragment_food_details_store_unit);
        lineChart = root.findViewById(R.id.fragment_food_details_chart);
        barChart = root.findViewById(R.id.fragment_food_details_histogram);

        strategy = new UnitAmountRenderStrategy();

        setupChart(dateRenderStrategy);
        setupHistogram();
    }

    void setAmounts(List<UnitAmount> unitAmounts) {
        amounts.setText(strategy.render(unitAmounts));
    }

    void setLocation(Optional<String> locationName) {
        location.setText(locationName.orElse("-"));
    }

    public void setDefaultExpiration(Period period) {
        int days = period.getDays();
        if (days == 0) {
            expirationOffset.setText("-");
        } else {
            expirationOffset.setText(days + "d");
        }
    }

    public void setUnit(ScaledUnitForSelection scaledUnitForSelection) {
        storeUnit.setText(strategy.render(scaledUnitForSelection));
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

    public void setDiagramData(List<PlotByUnit<LocalDateTime>> plotByUnits, @ColorInt int[] lineColour) {
        LineData data = new LineData();
        var yAxisAssigner = new YAxisAssigner(plotByUnits);
        int colourIndex = 0;
        int plotIndex = 0;
        for (var dataSource : plotByUnits) {
            var entries = dataSource.plotPoints().stream()
                    .map(v -> new Entry(dateRenderStrategy.toFloat(v.x()), v.y().floatValue()))
                    .collect(Collectors.toList());
            LineDataSet dataSet = new LineDataSet(entries,
                    dataSource.abbreviation() + yAxisAssigner.axisHintForLegend(plotIndex));
            dataSet.setColor(lineColour[colourIndex]);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.STEPPED);
            dataSet.setAxisDependency(yAxisAssigner.getAffinityOf(plotIndex));
            data.addDataSet(dataSet);
            colourIndex = (colourIndex + 1) % lineColour.length;
            plotIndex++;
        }
        data.setDrawValues(false);
        lineChart.setData(data);
        long maxX = (long) lineChart.getXAxis().getAxisMaximum();
        maxX = maxX + (86400L - maxX % 86400L);
        lineChart.getXAxis().setAxisMaximum(maxX);
        long minX = (long) lineChart.getXAxis().getAxisMinimum();
        minX = minX - (minX % 86400L);
        lineChart.getAxisRight().setEnabled(yAxisAssigner.needsTwoAxes());
        lineChart.getXAxis().setAxisMinimum(minX);
        lineChart.invalidate();
    }

    private void setupHistogram() {
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setGranularity(1);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setGranularity(1);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisLeft().setGridLineWidth(0.5f);
        barChart.setScaleXEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
    }

    public void setHistogramData(List<PlotPoint<Integer>> plotPoints, @ColorInt int lineColour) {
        var entries = plotPoints.stream()
                .map(v -> new BarEntry(v.x(), v.y().floatValue()))
                .collect(Collectors.toList());
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(lineColour);
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        float x = (barChart.getWidth() - barChart.getDescription().getXOffset()) / 2;
        float y = barChart.getViewPortHandler().offsetTop();
        barChart.getDescription().setPosition(x, y);
        barChart.invalidate();
    }
}
