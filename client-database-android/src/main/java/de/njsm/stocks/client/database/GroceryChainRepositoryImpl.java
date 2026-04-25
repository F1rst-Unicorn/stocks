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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.EntityDeleteRepository;
import de.njsm.stocks.client.business.GroceryChainRepository;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.VersionedId;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class GroceryChainRepositoryImpl implements EntityDeleteRepository<GroceryChain>, GroceryChainRepository {

    private final GroceryChainDao groceryChainDao;

    @Inject
    GroceryChainRepositoryImpl(GroceryChainDao groceryChainDao) {
        this.groceryChainDao = groceryChainDao;
    }

    @Override
    public GroceryChainForDeletion getEntityForDeletion(Id<GroceryChain> id) {
        GroceryChainDbEntity groceryChainEntity = groceryChainDao.getGroceryChainEntity(id);
        return GroceryChainForDeletion.create(groceryChainEntity.id(), groceryChainEntity.version());
    }

    @Override
    public Observable<List<GroceryChainForListing>> getGroceryChains() {
        return groceryChainDao.getGroceryChains()
                .distinctUntilChanged();
    }

    @Override
    public Observable<GroceryChainForEditing> getGroceryChain(Id<GroceryChain> id) {
        return groceryChainDao.getGroceryChainForEditing(id.id())
                .map(GroceryChainRepositoryImpl::map);
    }

    @Override
    public GroceryChainForEditing getCurrentGroceryChainBeforeEditing(Id<GroceryChain> formData) {
        return map(groceryChainDao.getGroceryChainEntity(formData));
    }

    private static GroceryChainForEditing map(GroceryChainDbEntity v) {
        return GroceryChainForEditing.create(
                VersionedId.create(v.id(), v.version()),
                v.name()
        );
    }
}
