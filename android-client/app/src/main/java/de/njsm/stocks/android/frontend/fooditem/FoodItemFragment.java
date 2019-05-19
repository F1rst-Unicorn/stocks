package de.njsm.stocks.android.frontend.fooditem;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.interactor.FoodItemDeletionInteractor;

import javax.inject.Inject;

public class FoodItemFragment extends BaseFragment {

    private FoodItemViewModel viewModel;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodItemAdapter adapter;

    private FoodItemFragmentArgs input;

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
        input = FoodItemFragmentArgs.fromBundle(getArguments());

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addItem);
        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodItemViewModel.class);
        viewModel.init(input.getFoodId());
        FoodViewModel foodViewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        LiveData<Food> selfFood = foodViewModel.getFood(input.getFoodId());
        selfFood.observe(this, u -> requireActivity().setTitle(u == null ? "" : u.name));

        adapter = new FoodItemAdapter(getResources(), requireActivity().getTheme(),
                viewModel.getFoodItems(), this::editItem);
        viewModel.getFoodItems().observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodItemDeletionInteractor interactor = new FoodItemDeletionInteractor(
                this, list,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteItem(i),
                i -> viewModel.getItem(i));
        addSwipeToDelete(list, viewModel.getFoodItems(), interactor::initiateDeletion, R.drawable.ic_local_dining_white_24dp);

        setHasOptionsMenu(true);
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_food_menu, menu);
    }

    private void addItem(View view) {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentAddFoodItem args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentAddFoodItem(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
    }

    private void editItem(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FoodItemFragmentDirections.ActionNavFragmentFoodItemToNavFragmentEanNumber args =
                FoodItemFragmentDirections.actionNavFragmentFoodItemToNavFragmentEanNumber(input.getFoodId());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(args);
        return true;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
