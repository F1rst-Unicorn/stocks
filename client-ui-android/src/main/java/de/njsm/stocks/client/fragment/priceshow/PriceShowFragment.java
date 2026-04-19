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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.PriceDetails;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.navigation.PriceShowNavigator;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.PriceShowViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

import java.util.function.Consumer;

import static de.njsm.stocks.client.fragment.fooddetails.FoodDetailsFragment.getLineColours;

public class PriceShowFragment extends InjectableFragment {

    private PriceShowViewModel viewModel;

    private PriceShowNavigator navigator;

    private PriceShowView priceShowView;

    private Localiser localiser;

    private IdImpl<Food> foodId;

    private Consumer<Boolean> swipeListener = v -> {};

    public PriceShowFragment(Consumer<Boolean> swipeListener) {
        this.swipeListener = swipeListener;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_price_show, container, false);
        foodId = navigator.getFoodId(requireArguments()).toId();
        root.findViewById(R.id.fragmet_price_show_add).setOnClickListener(v -> navigator.addPrice(foodId));
        root.findViewById(R.id.fragmet_price_show_show_all).setOnClickListener(v -> navigator.showAllPrices(foodId));

        root.findViewById(R.id.fragment_price_show_scroller).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeListener.accept(scrollY == 0);
        });

        priceShowView = new PriceShowView(root, this::getString, new DateRenderStrategy(localiser));
        viewModel.getPrices(foodId).observe(getViewLifecycleOwner(), this::onListDataReceived);

        return root;
    }

    private void onListDataReceived(PriceDetails priceForTableListings) {
        priceShowView.setTableData(priceForTableListings.pricesForTable(), localiser);
        priceShowView.setChartData(priceForTableListings.pricePlotByChain(), priceForTableListings.pricePlotByStore(),
                getLineColours(getResources(), requireActivity().getTheme()));
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(PriceShowViewModel.class);
    }

    @Inject
    void setNavigator(PriceShowNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
