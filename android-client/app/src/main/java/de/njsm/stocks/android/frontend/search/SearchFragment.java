package de.njsm.stocks.android.frontend.search;

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

import javax.inject.Inject;
import java.util.List;

public class SearchFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    private SearchAdapter adapter;

    private LiveData<List<FoodView>> data;

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
        SearchFragmentArgs input = SearchFragmentArgs.fromBundle(getArguments());

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        SearchViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
        data = viewModel.search(input.getSearchTerm());
        adapter = new SearchAdapter(data, this::onClick);
        data.observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, viewModelFactory);
        result.findViewById(R.id.template_swipe_list_fab).setVisibility(View.GONE);
        return result;
    }

    private void onClick(View view) {
        SearchAdapter.ViewHolder holder = (SearchAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<FoodView> list = data.getValue();
        if (list != null) {
            int id = list.get(position).id;
            SearchFragmentDirections.ActionNavFragmentSearchToNavFragmentFoodItem args =
                    SearchFragmentDirections.actionNavFragmentSearchToNavFragmentFoodItem(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
