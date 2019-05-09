package de.njsm.stocks.android.frontend.user;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.AndroidInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.frontend.BaseActivity;
import de.njsm.stocks.android.frontend.util.NameValidator;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.frontend.util.SwipeCallback;
import de.njsm.stocks.android.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;

public class UserActivity extends BaseActivity {

    private static final Logger LOG = new Logger(UserActivity.class);

    private RecyclerView view;

    private RecyclerView.Adapter<UserAdapter.ViewHolder> adapter;

    private UserViewModel viewModel;

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle(R.string.action_users);

        view = findViewById(R.id.users_list);
        view.setLayoutManager(new LinearLayoutManager(this));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        SwipeCallback<User> callback = new SwipeCallback<>(
                viewModel.getUsers().getValue(),
                this::initiateUserDeletion,
                ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent))
                );
        viewModel.getUsers().observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(view);

        adapter = new UserAdapter(viewModel.getUsers());
        viewModel.getUsers().observe(this, u -> adapter.notifyDataSetChanged());
        view.setAdapter(adapter);

        SwipeRefreshLayout refresher = findViewById(R.id.users_swipe);
        RefreshViewModel refreshViewModel = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);
        refresher.setOnRefreshListener(new SwipeSyncCallback(this, refresher, refreshViewModel));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.getUsers().removeObservers(this);
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    public void addUser(View view) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NameValidator(e -> textField.setError(getResources().getString(e))));
        textField.setHint(getResources().getString(R.string.hint_username));
        new AlertDialog.Builder(this)
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
                                result.observe(UserActivity.this, UserActivity.this::maybeShowDeleteError);
                                break;
                        }
                    }
                })
                .show();
    }

}
