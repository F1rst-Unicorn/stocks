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

package de.njsm.stocks.android.business.data.eventlog;

import de.njsm.stocks.android.db.entities.VersionedData;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public abstract class PartialDiffGenerator<T extends VersionedData> {

    private final IntFunction<String> stringResourceResolver;

    private final T oldEntity;

    private final T newEntity;

    private final SentenceObject object;

    public PartialDiffGenerator(IntFunction<String> stringResourceResolver, T oldEntity, T newEntity, SentenceObject object) {
        this.stringResourceResolver = stringResourceResolver;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
        this.object = object;
    }

    protected abstract boolean entitiesDiffer();

    protected abstract int getStringId();

    protected abstract Object[] getFormatArguments();

    public void generate(Consumer<String> consumer) {
        if (entitiesDiffer()) {
            String template = stringResourceResolver.apply(getStringId());
            String description = String.format(template, getFormatArguments());
            consumer.accept(description);
        }
    }

    protected T getOldEntity() {
        return oldEntity;
    }

    protected T getNewEntity() {
        return newEntity;
    }

    protected SentenceObject getObject() {
        return object;
    }
}