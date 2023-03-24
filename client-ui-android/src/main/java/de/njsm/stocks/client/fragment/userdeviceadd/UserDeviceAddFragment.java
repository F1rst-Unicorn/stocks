/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.fragment.userdeviceadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDeviceAddForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.SingleNameForm;
import de.njsm.stocks.client.navigation.UserDeviceAddNavigator;
import de.njsm.stocks.client.presenter.UserDeviceAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UserDeviceAddFragment extends BottomToolbarFragment implements MenuProvider {

	private UserDeviceAddViewModel viewModel;

	private UserDeviceAddNavigator navigator;

	private SingleNameForm form;

	private Id<User> user;

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);

		View result = insertContent(inflater, root, R.layout.fragment_user_device_form);
		form = new SingleNameForm(result, this::getString, R.id.fragment_user_device_form_name);

		user = navigator.getArguments(requireArguments());

		requireActivity().addMenuProvider(this, getViewLifecycleOwner());
		return root;
	}

	@Override
	public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.check, menu);
	}

	@Override
	public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
		if (!form.maySubmit()) {
			form.showErrors();
			return true;
		}

		UserDeviceAddForm data = UserDeviceAddForm.create(form.getName(), user);
		viewModel.add(data);
		navigator.back();
		return true;
	}

	@Inject
	@Override
	public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
		super.setViewModelFactory(viewModelFactory);
		ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
		viewModel = viewModelProvider.get(UserDeviceAddViewModel.class);
	}

	@Inject
	void setNavigator(UserDeviceAddNavigator navigator) {
		this.navigator = navigator;
	}
}
