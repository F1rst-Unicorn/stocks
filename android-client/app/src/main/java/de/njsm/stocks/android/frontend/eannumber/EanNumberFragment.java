package de.njsm.stocks.android.frontend.eannumber;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.zxing.integration.android.IntentIntegrator;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.EanNumberDeletionInteractor;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class EanNumberFragment extends BaseFragment {

    private static final Logger LOG = new Logger(EanNumberFragment.class);

    private ViewModelProvider.Factory viewModelFactory;

    private EanNumberViewModel viewModel;

    private RecyclerView list;

    private EanNumberAdapter adapter;

    private EanNumberBroadcastReceiver receiver;

    private EanNumberFragmentArgs input;

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

        assert getArguments() != null;
        input = EanNumberFragmentArgs.fromBundle(getArguments());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EanNumberViewModel.class);
        viewModel.init(input.getFoodId());

        adapter = new EanNumberAdapter(viewModel.getData(), this::doNothing);
        viewModel.getData().observe(this, v -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        EanNumberDeletionInteractor deleter = new EanNumberDeletionInteractor(this, list, v -> adapter.notifyDataSetChanged(), viewModel::deleteEanNumber);
        addSwipeToDelete(list, viewModel.getData(), deleter::initiateDeletion);
        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::startScanning);
        receiver = new EanNumberBroadcastReceiver(this::addEanNumber);
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_QR_CODE_SCANNED);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);
        initialiseSwipeRefresh(result, viewModelFactory);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    private void startScanning(View dummy) {
        LOG.i("Starting QR code reader");
        if (probeForCameraPermission()) {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    private void addEanNumber(String code) {
        if (code != null) {
            LiveData<StatusCode> result = viewModel.addEanNumber(code, input.getFoodId());
            result.observe(this, this::maybeShowAddError);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
