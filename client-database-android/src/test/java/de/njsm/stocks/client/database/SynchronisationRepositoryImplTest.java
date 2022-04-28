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

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.Update;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SynchronisationRepositoryImplTest {

    private SynchronisationRepositoryImpl uut;

    private SynchronisationDao synchronisationDao;

    @Before
    public void setUp() {
        synchronisationDao = mock(SynchronisationDao.class);
        uut = new SynchronisationRepositoryImpl(synchronisationDao);
    }

    @Test
    public void updateListIsReturned() {
        List<UpdateDbEntity> data = Arrays.asList(
                UpdateDbEntity.create(1, EntityType.LOCATION, Instant.MIN),
                UpdateDbEntity.create(2, EntityType.USER, Instant.MIN),
                UpdateDbEntity.create(3, EntityType.USER_DEVICE, Instant.MIN)
        );
        when(synchronisationDao.getAll()).thenReturn(data);

        List<Update> actual = uut.getUpdates();

        assertEquals(data.stream().map(DataMapper::map).collect(toList()), actual);
        verify(synchronisationDao).getAll();
    }

    @Test
    public void updatesToWriteAreForwarded() {
        uut.writeUpdates(emptyList());

        verify(synchronisationDao).writeUpdates(emptyList());
    }

    @Test
    public void locationsToWriteAreForwarded() {
        uut.writeLocations(emptyList());

        verify(synchronisationDao).writeLocations(emptyList());
    }

    @Test
    public void locationsToInitialiseAreForwarded() {
        uut.initialiseLocations(emptyList());

        verify(synchronisationDao).synchroniseLocations(emptyList());
    }

    @Test
    public void usersToWriteAreForwarded() {
        uut.writeUsers(emptyList());

        verify(synchronisationDao).writeUsers(emptyList());
    }

    @Test
    public void usersToInitialiseAreForwarded() {
        uut.initialiseUsers(emptyList());

        verify(synchronisationDao).synchroniseUsers(emptyList());
    }

    @Test
    public void userDevicesToWriteAreForwarded() {
        uut.writeUserDevices(emptyList());

        verify(synchronisationDao).writeUserDevices(emptyList());
    }

    @Test
    public void userDevicesToInitialiseAreForwarded() {
        uut.initialiseUserDevices(emptyList());

        verify(synchronisationDao).synchroniseUserDevices(emptyList());
    }

    @Test
    public void foodToWriteAreForwarded() {
        uut.writeFood(emptyList());

        verify(synchronisationDao).writeFood(emptyList());
    }

    @Test
    public void foodToInitialiseAreForwarded() {
        uut.initialiseFood(emptyList());

        verify(synchronisationDao).synchroniseFood(emptyList());
    }

    @Test
    public void eanNumberToWriteAreForwarded() {
        uut.writeEanNumbers(emptyList());

        verify(synchronisationDao).writeEanNumbers(emptyList());
    }

    @Test
    public void eanNumberToInitialiseAreForwarded() {
        uut.initialiseEanNumbers(emptyList());

        verify(synchronisationDao).synchroniseEanNumbers(emptyList());
    }

    @Test
    public void foodItemsToWriteAreForwarded() {
        uut.writeFoodItems(emptyList());

        verify(synchronisationDao).writeFoodItems(emptyList());
    }

    @Test
    public void foodItemsToInitialiseAreForwarded() {
        uut.initialiseFoodItems(emptyList());

        verify(synchronisationDao).synchroniseFoodItems(emptyList());
    }

    @Test
    public void unitsToWriteAreForwarded() {
        uut.writeUnits(emptyList());

        verify(synchronisationDao).writeUnits(emptyList());
    }

    @Test
    public void unitsToInitialiseAreForwarded() {
        uut.initialiseUnits(emptyList());

        verify(synchronisationDao).synchroniseUnits(emptyList());
    }

    @Test
    public void scaledUnitsToWriteAreForwarded() {
        uut.writeScaledUnits(emptyList());

        verify(synchronisationDao).writeScaledUnits(emptyList());
    }

    @Test
    public void scaledUnitsToInitialiseAreForwarded() {
        uut.initialiseScaledUnits(emptyList());

        verify(synchronisationDao).synchroniseScaledUnits(emptyList());
    }

    @Test
    public void recipesToWriteAreForwarded() {
        uut.writeRecipes(emptyList());

        verify(synchronisationDao).writeRecipes(emptyList());
    }

    @Test
    public void recipesToInitialiseAreForwarded() {
        uut.initialiseRecipes(emptyList());

        verify(synchronisationDao).synchroniseRecipes(emptyList());
    }

    @Test
    public void recipeIngredientsToWriteAreForwarded() {
        uut.writeRecipeIngredients(emptyList());

        verify(synchronisationDao).writeRecipeIngredients(emptyList());
    }

    @Test
    public void recipeIngredientsToInitialiseAreForwarded() {
        uut.initialiseRecipeIngredients(emptyList());

        verify(synchronisationDao).synchroniseRecipeIngredients(emptyList());
    }

    @Test
    public void recipeProductsToWriteAreForwarded() {
        uut.writeRecipeProducts(emptyList());

        verify(synchronisationDao).writeRecipeProducts(emptyList());
    }

    @Test
    public void recipeProductsToInitialiseAreForwarded() {
        uut.initialiseRecipeProducts(emptyList());

        verify(synchronisationDao).synchroniseRecipeProducts(emptyList());
    }
}
