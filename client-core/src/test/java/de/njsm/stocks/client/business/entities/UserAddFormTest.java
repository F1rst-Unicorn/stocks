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

package de.njsm.stocks.client.business.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserAddFormTest {

    @Test
    void nameCanBePassed() {
        assertDoesNotThrow(() -> UserAddForm.create("Joanna"));
        assertDoesNotThrow(() -> UserAddForm.create("John"));
    }

    @Test
    void dollarSignIsForbidden() {
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("$"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("$Joanna"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("Joanna$"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("Joanna$Mobile"));
    }

    @Test
    void equalsSignIsForbidden() {
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("Joanna=Mobile"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("="));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("=Joanna"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("Joanna="));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("CN=Joanna"));
    }

    @Test
    void spaceIsForbidden() {
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("John Doe"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create(" "));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create(" John"));
        assertThrows(IllegalArgumentException.class, () -> UserAddForm.create("John "));
    }
}