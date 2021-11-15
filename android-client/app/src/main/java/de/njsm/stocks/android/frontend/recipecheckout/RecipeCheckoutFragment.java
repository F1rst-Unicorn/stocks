package de.njsm.stocks.android.frontend.recipecheckout;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.RecipeFoodCheckout;
import de.njsm.stocks.android.frontend.InjectedFragment;

import java.util.List;

public class RecipeCheckoutFragment extends InjectedFragment {

    private ViewModel viewModel;

    private Adapter ingredientAdapter;

    private Adapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View result = inflater.inflate(R.layout.fragment_recipe_checkout, container, false);

        RecipeCheckoutFragmentArgs input = RecipeCheckoutFragmentArgs.fromBundle(getArguments());

        viewModel = getViewModelProvider().get(ViewModel.class);
        ingredientAdapter = initialiseItemList(viewModel.getIngredients(input.getRecipeId()), result.findViewById(R.id.fragment_recipe_checkout_ingredients));
        productAdapter = initialiseProductItemList(viewModel.getProducts(input.getRecipeId()), result.findViewById(R.id.fragment_recipe_checkout_products));

        return result;
    }

    private Adapter initialiseItemList(LiveData<List<RecipeFoodCheckout>> dataToBind, RecyclerView viewById) {
        viewById.setLayoutManager(new LinearLayoutManager(requireActivity()));
        Adapter adapter = new IngredientAdapter(getResourceProvider(), dataToBind, this::doNothing, viewModel::setFoodToBuyStatus);
        dataToBind.observe(getViewLifecycleOwner(), l -> adapter.notifyDataSetChanged());
        viewById.setAdapter(adapter);
        return adapter;
    }

    private Adapter initialiseProductItemList(LiveData<List<RecipeFoodCheckout>> dataToBind, RecyclerView viewById) {
        viewById.setLayoutManager(new LinearLayoutManager(requireActivity()));
        Adapter adapter = new ProductAdapter(getResourceProvider(), dataToBind, this::doNothing, viewModel::setFoodToBuyStatus);
        dataToBind.observe(getViewLifecycleOwner(), l -> adapter.notifyDataSetChanged());
        viewById.setAdapter(adapter);
        return adapter;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_recipe_checkout_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fragment_recipe_checkout_options_checkout) {
            doCheckout();
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigateUp();
        }
        return true;
    }

    private void doCheckout() {
        List<Adapter.FormDataItem> foodToCheckOut = ingredientAdapter.collectData();
        List<Adapter.FormDataItem> foodToAdd = productAdapter.collectData();
        viewModel.checkoutFood(foodToCheckOut, foodToAdd);
    }
}
