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

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.SearchRepository;

import java.io.Serializable;
import java.time.Duration;

@AutoValue
public abstract class RecipeEditBaseData implements Id<Recipe>, Serializable {

    private static final long serialVersionUID = 1L;

    public abstract String name();

    public abstract String instructions();

    public abstract Duration duration();

    public static RecipeEditBaseData create(int id, String name, String instructions, Duration duration) {
        return new AutoValue_RecipeEditBaseData(id, name, instructions, duration);
    }
}
