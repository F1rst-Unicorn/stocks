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

import de.njsm.stocks.client.business.EntityDeleteService;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.UnitAddService;
import de.njsm.stocks.client.business.UnitEditService;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.servertest.v2.repo.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Order(900)
public class UnitTest extends Base {

    private UnitAddService unitAddService;

    private UnitEditService unitEditService;

    private EntityDeleteService<Unit> unitDeleteService;

    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        dagger.inject(this);
    }

    @Test
    void addAnItem() {
        UnitAddForm input = UnitAddForm.create("Liter", "l");

        unitAddService.addUnit(input);

        var units = updateService.getUnits(Instant.EPOCH);
        assertThat(units).filteredOn(UnitForSynchronisation::name, input.name())
                .isNotEmpty()
                .allMatch(v -> v.abbreviation().equals(input.abbreviation()));
    }

    @Test
    void rename() {
        String newName = "Cabal";
        String newAbbreviation = "fdsa";
        IdImpl<Unit> id = unitRepository.createNew("Gramm", "g");

        unitEditService.edit(UnitForEditing.builder()
                .id(id.id())
                .version(0)
                .name(newName)
                .abbreviation(newAbbreviation)
                .build());

        var units = updateService.getUnits(Instant.EPOCH);
        assertThat(units).filteredOn(UnitForSynchronisation::name, newName)
                .isNotEmpty()
                .allMatch(v -> v.abbreviation().equals(newAbbreviation));
    }

    @Test
    void renamingFailsWithWrongVersion() {
        String newName = "Cabal";
        String newAbbreviation = "fdsa";
        IdImpl<Unit> id = unitRepository.createNew("Gramm", "g");

        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> unitEditService.edit(UnitForEditing.builder()
                        .id(id.id())
                        .version(99)
                        .name(newName)
                        .abbreviation(newAbbreviation)
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.INVALID_DATA_VERSION);
    }

    @Test
    void renamingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> unitEditService.edit(UnitForEditing.builder()
                        .id(9999)
                        .version(0)
                        .name("newName")
                        .abbreviation("newAbbreviation")
                        .build()))
                .matches(v -> v.getStatusCode() == StatusCode.NOT_FOUND);
    }

    @Test
    void delete() {
        String name = "Cookie";
        IdImpl<Unit> id = unitRepository.createNew(name, "fdsa");

        unitDeleteService.delete(UnitForDeletion.create(id.id(), 0));

        List<UnitForSynchronisation> locations = updateService.getUnits(Instant.EPOCH);
        assertThat(locations).filteredOn(UnitForSynchronisation::name, name)
                .isNotEmpty()
                .anyMatch(v -> v.transactionTimeEnd().isBefore(INFINITY));
    }

    @Test
    void deletingFailsWithWrongVersion() {
        IdImpl<Unit> id = unitRepository.createNew("Cookie", "fdsa");

        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> unitDeleteService.delete(UnitForDeletion.create(id.id(), 99)))
                .matches(v -> v.getStatusCode() == StatusCode.INVALID_DATA_VERSION);
    }

    @Test
    void deletingUnknownIdIsReported() {
        assertThatExceptionOfType(StatusCodeException.class)
                .isThrownBy(() -> unitDeleteService.delete(UnitForDeletion.create(9999, 0)))
                .matches(v -> v.getStatusCode() == StatusCode.NOT_FOUND);
    }

    @Inject
    void setUnitAddService(UnitAddService unitAddService) {
        this.unitAddService = unitAddService;
    }

    @Inject
    void setUnitRepository(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Inject
    void setUnitDeleteService(EntityDeleteService<Unit> unitDeleteService) {
        this.unitDeleteService = unitDeleteService;
    }

    @Inject
    void setUnitEditService(UnitEditService unitEditService) {
        this.unitEditService = unitEditService;
    }
}
