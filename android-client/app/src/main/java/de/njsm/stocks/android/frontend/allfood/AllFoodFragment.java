package de.njsm.stocks.android.frontend.allfood;

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
import de.njsm.stocks.android.frontend.StringListAdapter;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;

import javax.inject.Inject;
import java.util.List;

public class AllFoodFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    private FoodViewModel viewModel;

    private FoodAdapter adapter;

    private LiveData<List<Food>> data;

    private AllFoodFragmentArgs input;

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
        input = AllFoodFragmentArgs.fromBundle(getArguments());

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FoodViewModel.class);
        data = viewModel.getFood();

        adapter = new FoodAdapter(data, this::onClick);
        data.observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    private void onClick(View view) {
        StringListAdapter.ViewHolder holder = (StringListAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Food> data = this.data.getValue();
        if (data != null) {
            int id = data.get(position).id;
            AllFoodFragmentDirections.ActionNavFragmentAllFoodToNavFragmentFoodItem args =
                    AllFoodFragmentDirections.actionNavFragmentAllFoodToNavFragmentFoodItem(id)
                    .setEanNumber(input.getEanNumber());
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
