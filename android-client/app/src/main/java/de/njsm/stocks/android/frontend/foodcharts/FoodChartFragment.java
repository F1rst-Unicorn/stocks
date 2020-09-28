/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.frontend.foodcharts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.foodhistory.FoodHistoryFragmentArgs;

public class FoodChartFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_food_charts, container, false);

        assert getArguments() != null;
        FoodHistoryFragmentArgs input = FoodHistoryFragmentArgs.fromBundle(getArguments());

        PlotViewModel plotViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlotViewModel.class);
        plotViewModel.init(input.getFoodId());
        LineChart chart = result.findViewById(R.id.fragment_food_charts_chart);
        setupChart(chart);
        plotViewModel.getHistory().observe(getViewLifecycleOwner(), l -> updateChart(chart, l));

        BarChart histogram = result.findViewById(R.id.fragment_food_charts_histogram);
        setupHistogram(histogram);
        plotViewModel.getHistogramData().observe(getViewLifecycleOwner(), l -> updateHistogram(histogram, l));

        initialiseSwipeRefresh(result, R.id.fragment_food_charts_swipe, viewModelFactory);
        return result;
    }

    private void updateHistogram(BarChart histogram, List<BarEntry> l) {
        BarDataSet dataSet = new BarDataSet(l, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireContext().getTheme()));
        BarData data = new BarData(dataSet);
        histogram.setData(data);
        float x = (histogram.getWidth() - histogram.getDescription().getXOffset()) / 2;
        float y = histogram.getViewPortHandler().offsetTop();
        histogram.getDescription().setPosition(x, y);
        histogram.invalidate();
    }

    private void setupHistogram(BarChart histogram) {
        histogram.getDescription().setEnabled(false);
        histogram.getLegend().setEnabled(false);
        histogram.getXAxis().setDrawLabels(true);
        histogram.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        histogram.getXAxis().setDrawGridLines(false);
        histogram.getXAxis().setGranularity(1);
        histogram.getAxisRight().setEnabled(false);
        histogram.getAxisLeft().setGranularity(1);
        histogram.getAxisLeft().setAxisMinimum(0);
        histogram.getAxisLeft().setGridLineWidth(0.5f);
        histogram.setScaleXEnabled(true);
        histogram.setScaleYEnabled(false);
        histogram.setDoubleTapToZoomEnabled(false);
        histogram.setHighlightPerDragEnabled(false);
        histogram.setHighlightPerTapEnabled(false);
    }

    private void setupChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new ValueFormatter());
        chart.setDrawBorders(false);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setHardwareAccelerationEnabled(true);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getXAxis().setGranularity(86400f);
        chart.getXAxis().setGridLineWidth(0.5f);
        chart.setXAxisRenderer(new AxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
    }

    private void updateChart(LineChart chart, List<Entry> l) {
        LineDataSet dataSet = new LineDataSet(l, "");
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setCircleColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, requireContext().getTheme()));
        dataSet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireContext().getTheme()));
        LineData data = new LineData(dataSet);
        data.setDrawValues(false);
        chart.setData(data);
        long minXx = (long) chart.getXAxis().getAxisMinimum();
        minXx = minXx - (minXx % 86400L);
        chart.getXAxis().setAxisMinimum(minXx);
        long maxXx = (long) chart.getXAxis().getAxisMaximum();
        maxXx = maxXx + (86400L - maxXx % 86400L);
        chart.getXAxis().setAxisMaximum(maxXx);
        chart.invalidate();
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
