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

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.entities.VersionedData;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public abstract class ChangedEntityEvent<T extends VersionedData> extends EntityEvent<T> {

    final T oldEntity;

    final T newEntity;

    public ChangedEntityEvent(User initiatorUser, UserDevice initiatorDevice, T oldEntity, T newEntity) {
        super(initiatorUser, initiatorDevice);
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        SentenceObject object = new SentenceObject(getExplicitObject(),
                stringResourceResolver.apply(R.string.event_enumeration_undefined_object),
                String.format(stringResourceResolver.apply(R.string.event_enumeration_undefined_object_genitive), getExplicitObject()));

        ArrayList<String> partialSentences = new ArrayList<>();
        for (PartialDiffGenerator<T> differ : getDiffers(stringResourceResolver, object)) {
            differ.generate(partialSentences::add);
        }

        StringBuilder result = new StringBuilder();
        result.append(initiatorUser.name);
        result.append(" ");

        String enumerationSeparator = stringResourceResolver.apply(R.string.event_enumeration_item_divider);
        for (int i = 0; i < partialSentences.size() - 1; i++) {
            result.append(partialSentences.get(i));
            result.append(enumerationSeparator);
            result.append(" ");
        }

        if (partialSentences.size() > 1) {
            result.deleteCharAt(result.length() - 2);
            result.append(stringResourceResolver.apply(R.string.event_enumeration_item_divider_last));
            result.append(" ");
        }

        result.append(partialSentences.get(partialSentences.size() - 1));
        result.append(stringResourceResolver.apply(R.string.event_end_of_sentence));

        return result.toString();
    }

    protected abstract List<PartialDiffGenerator<T>> getDiffers(IntFunction<String> stringResourceResolver, SentenceObject object);

    protected abstract String getExplicitObject();

    @Override
    public Instant getTime() {
        return oldEntity.transactionTimeStart;
    }

    @Override
    public int getEventIconResource() {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public T getEntity() {
        return oldEntity;
    }
}
