/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.frontend.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.frontend.InjectedFragment;
import de.njsm.stocks.android.frontend.interactor.UserDeletionInteractor;
import de.njsm.stocks.android.frontend.util.NameValidator;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.common.api.StatusCode;

import java.util.List;

public class UserFragment extends InjectedFragment {

    RecyclerView.Adapter<UserAdapter.ViewHolder> adapter;

    UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        RecyclerView list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        adapter = new UserAdapter(viewModel.getUsers(), this::onListItemClick);
        viewModel.getUsers().observe(getViewLifecycleOwner(), u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        UserDeletionInteractor interactor = new UserDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteUser(i));
        addSwipeToDelete(list, viewModel.getUsers(), interactor::initiateDeletion);

        initialiseSwipeRefresh(result, viewModelFactory);

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addUser);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getUsers().removeObservers(this);
    }

    private void onListItemClick(View view) {
        UserAdapter.ViewHolder holder = (UserAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<User> list = viewModel.getUsers().getValue();
        if (list != null) {
            int userId = list.get(position).id;
            UserFragmentDirections.ActionNavFragmentUsersToNavFragmentDevices args =
                    UserFragmentDirections.actionNavFragmentUsersToNavFragmentDevices(userId);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
        }
    }

    private void addUser(View view) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NameValidator(e -> textField.setError(getResources().getString(e))));
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
        textField.setHint(getResources().getString(R.string.hint_username));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_user))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addUser(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), this::doNothing)
                .show();
    }

}
