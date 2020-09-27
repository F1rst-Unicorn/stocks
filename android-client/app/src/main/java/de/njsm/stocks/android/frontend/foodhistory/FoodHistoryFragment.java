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

package de.njsm.stocks.android.frontend.foodhistory;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;

public class FoodHistoryFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_food_history, container, false);

        assert getArguments() != null;
        FoodHistoryFragmentArgs input = FoodHistoryFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.fragment_food_history_fab).setVisibility(View.GONE);

        RecyclerView list = result.findViewById(R.id.fragment_food_history_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        EventViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(EventViewModel.class);
        viewModel.init(input.getFoodId());
        EventAdapter adapter = new EventAdapter(getResources(), requireActivity().getTheme(), requireContext()::getString);
        viewModel.getHistory().observe(getViewLifecycleOwner(), adapter::submitList);
        list.setAdapter(adapter);

        FoodViewModel foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        foodViewModel.initFood(input.getFoodId());
        foodViewModel.getFood().observe(getViewLifecycleOwner(), u -> requireActivity().setTitle(u == null ? "" : String.format(getString(R.string.title_food_activity), u.name)));

        PlotViewModel plotViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlotViewModel.class);
        plotViewModel.init(input.getFoodId());
        LineChart chart = result.findViewById(R.id.fragment_food_history_chart);
        setupChart(chart);

        plotViewModel.getHistory().observe(getViewLifecycleOwner(), l -> {
            updateChart(chart, l);
        });

        initialiseSwipeRefresh(result, R.id.fragment_food_history_swipe, viewModelFactory);
        return result;
    }

    private void setupChart(LineChart chart) {
        chart.getDescription().setText("");
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
