package de.njsm.stocks.android.frontend.main;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.frontend.util.SwipeSyncCallback;

import javax.inject.Inject;

public class OutlineFragment extends BaseFragment {


    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_outline, container, false);


        SwipeRefreshLayout swiper = result.findViewById(R.id.fragment_outline_swipe);
        RefreshViewModel refresher = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);
        swiper.setOnRefreshListener(new SwipeSyncCallback(this, swiper, refresher));

        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
