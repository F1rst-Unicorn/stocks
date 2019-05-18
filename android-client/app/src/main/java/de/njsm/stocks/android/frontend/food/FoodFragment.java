package de.njsm.stocks.android.frontend.food;

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
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.FoodDeletionInteractor;
import de.njsm.stocks.android.frontend.locations.LocationViewModel;

import javax.inject.Inject;
import java.util.List;

public class FoodFragment extends BaseFragment {

    private RecyclerView list;

    private ViewModelProvider.Factory viewModelFactory;

    private FoodToEatViewModel viewModel;

    private LiveData<List<FoodView>> data;

    private LocationViewModel locationViewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodToEatViewModel.class);
        locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);

        if (getArguments() != null) {
            FoodFragmentArgs input = FoodFragmentArgs.fromBundle(getArguments());
            if (input.getLocation() != 0) {
                data = viewModel.getFoodByLocation(input.getLocation());
                locationViewModel.getLocation(input.getLocation()).observe(this,
                        d -> requireActivity().setTitle(d.name));
            } else {
                data = viewModel.getFoodToEat();
                requireActivity().setTitle(R.string.action_eat_next);
            }
        }

        FoodAdapter adapter = new FoodAdapter(data,
                this::onClick, getResources(), requireActivity().getTheme());
        data.observe(this, i -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        FoodDeletionInteractor interactor = new FoodDeletionInteractor(
                this, list,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteFood(i),
                i -> viewModel.getFood(i));
        addSwipeToDelete(list, data, v -> interactor.initiateDeletion(v.mapToFood()));

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(v -> addFood(viewModel));
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    private void onClick(View view) {
        FoodAdapter.ViewHolder holder = (FoodAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodView> data = this.data.getValue();
        if (data != null) {
            int id = data.get(position).id;
            FoodFragmentDirections.ActionNavFragmentFoodToNavFragmentFoodItem args =
                    FoodFragmentDirections.actionNavFragmentFoodToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
