package de.njsm.stocks.android.frontend.emptyfood;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;

public class EmptyFoodFragment extends BaseFragment {

    private RecyclerView list;

    FoodAdapter adapter;

    EmptyFoodViewModel viewModel;

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EmptyFoodViewModel.class);
        addSwipeToDelete(list, viewModel.getFood(), this::initiateFoodDeletion);

        adapter = new FoodAdapter(viewModel.getFood(), this::onListItemClicked);
        viewModel.getFood().observe(this, u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, viewModelFactory);
        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(v -> this.addFood(viewModel));
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void onListItemClicked(View view) {

    }

    private void initiateFoodDeletion(Food food) {
        showDeletionSnackbar(list, food,
                R.string.dialog_food_was_deleted,
                f -> adapter.notifyDataSetChanged(), this::performDeletion
        );
    }

    private void performDeletion(Food food) {
        adapter.notifyDataSetChanged();
        LiveData<StatusCode> result = viewModel.deleteFood(food);
        result.observe(EmptyFoodFragment.this, c -> EmptyFoodFragment.this.treatDeletionCases(c, food));
    }

    void treatDeletionCases(StatusCode code, Food item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = viewModel.getFood(item.id);
            newData.observe(this, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareFood(item, newItem);
                    newData.removeObservers(this);
                }
            });
        } else
            maybeShowDeleteError(code);
    }

    private void compareFood(Food item, Food upstreamItem) {
        String message = requireContext().getString(R.string.error_food_changed, item.name, upstreamItem.name);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_delete_food)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    LiveData<StatusCode> result = viewModel.deleteFood(upstreamItem);
                    result.observe(this, this::maybeShowDeleteError);
                    d.dismiss();
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }
}
