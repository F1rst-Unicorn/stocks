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

package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

@AutoValue
@JsonDeserialize(builder = AutoValue_UserDeviceForInsertion.Builder.class)
public abstract class UserDeviceForInsertion implements Insertable<UserDevice>, SelfValidating {

    @JsonGetter
    public abstract String name();

    @JsonGetter
    public abstract int belongsTo();

    public static Builder builder() {
        return new AutoValue_UserDeviceForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder
    public abstract static class Builder
            extends SelfValidating.Builder<UserDeviceForInsertion> {

        public abstract Builder name(String v);

        public abstract Builder belongsTo(int v);
    }

    @Override
    public boolean isContainedIn(UserDevice entity) {
        return name().equals(entity.name()) &&
                belongsTo() == entity.belongsTo();
    }

    @Override
    public void validate() {
        Preconditions.checkState(belongsTo() > 0, "belongsTo id is invalid");
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.userDeviceForInsertion(this, argument);
    }
}
