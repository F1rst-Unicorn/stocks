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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodDetails;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.navigation.FoodDetailsNavigator;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.FoodDetailsViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.function.Consumer;

public class FoodDetailsFragment extends InjectableFragment {

    private FoodDetailsViewModel viewModel;

    private FoodDetailsNavigator navigator;

    private FoodDetailsView view;

    private DateRenderStrategy dateRenderStrategy;

    private Consumer<Boolean> swipeListener = v -> {};

    public FoodDetailsFragment() {}

    public FoodDetailsFragment(Consumer<Boolean> swipeListener) {
        this.swipeListener = swipeListener;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_food_details, container, false);

        root.findViewById(R.id.fragment_food_details_scroller).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeListener.accept(scrollY == 0);
        });

        view = new FoodDetailsView(root, dateRenderStrategy);
        Id<Food> food = navigator.getId(requireArguments());
        viewModel.getData(food).observe(getViewLifecycleOwner(), this::setData);

        return root;
    }

    private void setData(FoodDetails foodDetails) {
        view.setAmounts(foodDetails.displayedAmount());
        view.setLocation(foodDetails.locationName());
        view.setDefaultExpiration(foodDetails.expirationOffset());
        view.setUnit(foodDetails.storeUnit());
        view.setDescription(foodDetails.description());
        view.setDiagramData(foodDetails.amountOverTime(), new int[]{
                ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireContext().getTheme()),
                ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, requireContext().getTheme()),
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, requireContext().getTheme()),
                ResourcesCompat.getColor(getResources(), R.color.colorOnSurface, requireContext().getTheme()),
                ResourcesCompat.getColor(getResources(), R.color.colorError, requireContext().getTheme())
        });
        view.setHistogramData(foodDetails.eatenAmountByExpiration(),
                ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireContext().getTheme()));
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(FoodDetailsViewModel.class);
    }

    @Inject
    void setNavigator(FoodDetailsNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setDateRenderStrategy(DateRenderStrategy dateRenderStrategy) {
        this.dateRenderStrategy = dateRenderStrategy;
    }
}
