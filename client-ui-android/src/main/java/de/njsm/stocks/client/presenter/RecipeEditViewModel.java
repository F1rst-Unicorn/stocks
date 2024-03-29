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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.RecipeEditInteractor;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.business.entities.RecipeEditForm;
import de.njsm.stocks.client.business.entities.RecipeEditFormData;

import javax.inject.Inject;

public class RecipeEditViewModel extends ViewModel {

    private final RecipeEditInteractor interactor;

    @Inject
    RecipeEditViewModel(RecipeEditInteractor interactor) {
        this.interactor = interactor;
    }

    public LiveData<RecipeEditFormData> get(Id<Recipe> id) {
        return LiveDataReactiveStreams.fromPublisher(interactor.getForm(id).toFlowable());
    }

    public void submit(RecipeEditForm form) {
        interactor.edit(form);
    }
}
