package de.njsm.stocks.android.frontend.emptyfood;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.njsm.stocks.android.frontend.interactor.FoodDeletionInteractor;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;
import java.util.List;

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

        adapter = new FoodAdapter(viewModel.getFood(),
                this::onListItemClicked,
                v -> editInternally(v, viewModel.getFood(), R.string.dialog_rename_food,
                        this::observeRenaming));
        viewModel.getFood().observe(this, u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodDeletionInteractor deleter = new FoodDeletionInteractor(this, list,
                f -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteFood(i),
                id -> viewModel.getFood(id));
        addSwipeToDelete(list, viewModel.getFood(), deleter::initiateDeletion);

        initialiseSwipeRefresh(result, viewModelFactory);
        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(v -> this.addFood(viewModel));
        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void onListItemClicked(View view) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Food> data = viewModel.getFood().getValue();
        if (data != null) {
            int id = data.get(position).id;
            EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentFood args =
                    EmptyFoodFragmentDirections.actionNavFragmentEmptyFoodToNavFragmentFood(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    private void observeRenaming(Food item, String name) {
        LiveData<StatusCode> result = viewModel.renameFood(item, name);
        result.observe(this, code -> this.treatRenamingCases(code, item, name));
    }

    private void treatRenamingCases(StatusCode code, Food item, String name) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = viewModel.getFood(item.id);
            newData.observe(this, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareFood(item, name, newItem);
                    newData.removeObservers(this);
                }
            });
        } else
            maybeShowEditError(code);
    }

    private void compareFood(Food item, String localNewName, Food upstreamItem) {
        String message = requireContext().getString(R.string.error_food_changed_twice, item.name, localNewName, upstreamItem.name);
        showErrorDialog(R.string.dialog_rename_food, message, (d,w) -> observeRenaming(upstreamItem, localNewName));
    }
}
