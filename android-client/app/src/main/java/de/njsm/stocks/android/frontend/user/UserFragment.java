package de.njsm.stocks.android.frontend.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.NameValidator;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.frontend.util.SwipeCallback;
import de.njsm.stocks.android.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class UserFragment extends BaseFragment {

    static final Logger LOG = new Logger(UserFragment.class);

    private RecyclerView view;

    RecyclerView.Adapter<UserAdapter.ViewHolder> adapter;

    UserViewModel viewModel;

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_users, container, false);
        FragmentActivity activity = getActivity();
        if (activity == null) return result;

        view = result.findViewById(R.id.users_list);
        view.setLayoutManager(new LinearLayoutManager(activity));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        SwipeCallback<User> callback = new SwipeCallback<>(
                viewModel.getUsers().getValue(),
                this::initiateUserDeletion,
                ContextCompat.getDrawable(activity, R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(activity, R.color.colorAccent))
        );
        viewModel.getUsers().observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(view);

        adapter = new UserAdapter(viewModel.getUsers());
        viewModel.getUsers().observe(this, u -> adapter.notifyDataSetChanged());
        view.setAdapter(adapter);

        SwipeRefreshLayout refresher = result.findViewById(R.id.users_swipe);
        RefreshViewModel refreshViewModel = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);
        refresher.setOnRefreshListener(new SwipeSyncCallback(this, refresher, refreshViewModel));

        result.findViewById(R.id.users_fab).setOnClickListener(this::addUser);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getUsers().removeObservers(this);
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void addUser(View view) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NameValidator(e -> textField.setError(getResources().getString(e))));
        textField.setHint(getResources().getString(R.string.hint_username));
        new AlertDialog.Builder(activity)
                .setTitle(getResources().getString(R.string.dialog_new_user))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    dialog.dismiss();
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addUser(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }

    private void initiateUserDeletion(User u) {
        Snackbar.make(view, R.string.dialog_user_was_deleted, Snackbar.LENGTH_SHORT)
                .setAction(R.string.action_undo, v -> {})
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        switch (event) {
                            case DISMISS_EVENT_ACTION:
                                adapter.notifyDataSetChanged();
                                LOG.d("Deletion cancelled");
                                break;
                            case DISMISS_EVENT_CONSECUTIVE:
                            case DISMISS_EVENT_MANUAL:
                            case DISMISS_EVENT_SWIPE:
                            case DISMISS_EVENT_TIMEOUT:
                                adapter.notifyDataSetChanged();
                                LiveData<StatusCode> result = viewModel.deleteUser(u);
                                result.observe(UserFragment.this, UserFragment.this::maybeShowDeleteError);
                                break;
                        }
                    }
                })
                .show();
    }

}
