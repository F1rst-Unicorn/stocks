package de.njsm.stocks.android.frontend.fooditem;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Config;

import javax.inject.Inject;

public class FoodFragment extends BaseFragment {

    private RecyclerView list;

    private FoodFragmentArgs input;

    private FoodViewModel foodViewModel;

    private FoodItemViewModel viewModel;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodItemAdapter adapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        assert getArguments() != null;
        input = FoodFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addItem);
        list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        viewModel.init(input.getFoodId());
        foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        LiveData<Food> selfFood = foodViewModel.getFood(input.getFoodId());
        selfFood.observe(this, u -> requireActivity().setTitle(u == null ? "" : u.name));
        addSwipeToDelete(list, viewModel.getFoodItems(), this::initiateDeletion);

        adapter = new FoodItemAdapter(getResources(), requireActivity().getTheme(),
                viewModel.getFoodItems(), this::editItem);
        viewModel.getFoodItems().observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        setHasOptionsMenu(true);
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_food_menu, menu);
    }

    private void addItem(View view) {

    }

    private void editItem(View view) {

    }

    private void initiateDeletion(FoodItemView t) {
        showDeletionSnackbar(list, t,
                R.string.dialog_food_was_deleted,
                f -> adapter.notifyDataSetChanged(),
                this::observeDeletion);
    }

    private void observeDeletion(FoodItemView t) {
        adapter.notifyDataSetChanged();
        LiveData<StatusCode> result = viewModel.deleteItem(t);
        result.observe(this, c -> FoodFragment.this.treatDeletionCases(c, t));
    }

    private void treatDeletionCases(StatusCode code, FoodItemView item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<FoodItemView> newData = viewModel.getItem(item.id);
            newData.observe(this, newItem -> {
                if (newItem != null && ! newItem.equals(item)) {
                    compareItems(item, newItem);
                    newData.removeObservers(this);
                }
            });
        } else
            maybeShowDeleteError(code);
    }

    private void compareItems(FoodItemView item, FoodItemView newItem) {
        boolean datesEqual = item.getEatByDate().equals(newItem.getEatByDate());
        boolean locationsEqual = item.getLocation().equals(newItem.getLocation());

        String message;
        if (datesEqual) {
            if (locationsEqual) {
                return;
            } else {
                message = getString(R.string.dialog_item_location_changed,
                        item.getLocation(),
                        newItem.getLocation());
            }
        } else {
            if (locationsEqual) {
                message = getString(R.string.dialog_item_date_changed,
                        item.getEatByDate(),
                        newItem.getEatByDate());

            } else {
                message = getString(R.string.dialog_item_both_changed,
                        item.getLocation(),
                        newItem.getLocation(),
                        Config.PRETTY_FORMAT.format(item.getEatByDate()),
                        Config.PRETTY_FORMAT.format(newItem.getEatByDate()));
            }
        }
        showErrorDialog(R.string.title_consume,
                message,
                (d,w) -> observeDeletion(newItem));
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
