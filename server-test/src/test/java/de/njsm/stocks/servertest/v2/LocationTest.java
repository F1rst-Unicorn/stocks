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

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.servertest.v2.repo.FoodRepository;
import de.njsm.stocks.servertest.v2.repo.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Order(600)
public class LocationTest extends Base {

    private LocationAddService locationAddService;

    private LocationEditService locationEditService;

    private EntityDeleteService<Location> locationDeleteService;

    private LocationRepository locationRepository;

    @BeforeEach
    void inject() {
        dagger.inject(this);
    }

    @Test
    void addAnItem() {
        String name = getUniqueName("addAnItem");

        locationAddService.add(LocationAddForm.create(name, ""));

        List<LocationForSynchronisation> locations = updateService.getLocations(Instant.EPOCH);
        assertThat(locations).filteredOn(LocationForSynchronisation::name, name)
                .isNotEmpty()
                .allMatch(v -> v.initiates() == 1);
    }

    @Test
    void renameLocation() {
        String name = getUniqueName("renameLocation");
        String newName = getUniqueName("renameLocation2");
        IdImpl<Location> id = locationRepository.createNewLocationType(name);
        LocationForEditing input = LocationForEditing.builder()
                .id(id.id())
                .version(0)
                .name(newName)
                .description("new description")
                .build();

        locationEditService.editLocation(input);

        List<LocationForSynchronisation> locations = updateService.getLocations(Instant.EPOCH);
        assertThat(locations).filteredOn(LocationForSynchronisation::name, newName)
                .isNotEmpty()
                .allMatch(v -> v.description().equals(input.description()));
    }

    @Test
    void renamingFailsWithWrongVersion() {
        String name = getUniqueName("renamingFailsWithWrongVersion");
        String newName = getUniqueName("renamingFailsWithWrongVersion2");
        IdImpl<Location> id = locationRepository.createNewLocationType(name);

        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> locationEditService.editLocation(LocationForEditing.builder()
                        .id(id.id())
                        .version(99)
                        .name(newName)
                        .description("")
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.INVALID_DATA_VERSION);
    }

    @Test
    void renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> locationEditService.editLocation(LocationForEditing.builder()
                        .id(9999)
                        .version(0)
                        .name("Location2")
                        .description("")
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.NOT_FOUND);
    }

    @Test
    void deleteLocation() {
        String name = "Location1";
        IdImpl<Location> id = locationRepository.createNewLocationType(name);

        locationDeleteService.delete(LocationForDeletion.builder()
                .id(id.id())
                .version(0)
                .build());

        List<LocationForSynchronisation> locations = updateService.getLocations(Instant.EPOCH);
        assertThat(locations).filteredOn(LocationForSynchronisation::name, name)
                .isNotEmpty()
                .anyMatch(v -> v.transactionTimeEnd().isBefore(INFINITY));
    }

    @Test
    void deletingFailsWithWrongVersion() {
        String name = "Location1";
        IdImpl<Location> id = locationRepository.createNewLocationType(name);

        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> locationDeleteService.delete(LocationForDeletion.builder()
                        .id(id.id())
                        .version(99)
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.INVALID_DATA_VERSION);
    }

    @Test
    void deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> locationDeleteService.delete(LocationForDeletion.builder()
                        .id(99999)
                        .version(0)
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.NOT_FOUND);
    }

    @Test
    void deleteWhileContainingFoodFails() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("cascadingTest");
        int foodId = FoodRepository.getAnyFoodId();
        FoodItemTest.createNewItem(Instant.EPOCH, locationId.id(), foodId);

        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> locationDeleteService.delete(LocationForDeletion.builder()
                        .id(locationId.id())
                        .version(0)
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION);
    }

    @Inject
    void setLocationAddService(LocationAddService locationAddService) {
        this.locationAddService = locationAddService;
    }

    @Inject
    void setLocationEditService(LocationEditService locationEditService) {
        this.locationEditService = locationEditService;
    }

    @Inject
    void setLocationDeleteService(EntityDeleteService<Location> locationDeleteService) {
        this.locationDeleteService = locationDeleteService;
    }

    @Inject
    void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
}
