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

package de.njsm.stocks.client.business.event;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;

public abstract class EventKeyHint {

    @AutoValue
    public static abstract class Location extends EventKeyHint {
        public abstract IdImpl<de.njsm.stocks.client.business.entities.Location> id();

        @Override
        protected <I, O> O visit(Visitor<I, O> visitor, I input) {
            return visitor.location(this, input);
        }

        public static Location of(Id<de.njsm.stocks.client.business.entities.Location> location) {
            return new AutoValue_EventKeyHint_Location(IdImpl.from(location));
        }
    }

    @AutoValue
    public static abstract class Food extends EventKeyHint {
        public abstract IdImpl<de.njsm.stocks.client.business.entities.Food> id();

        @Override
        protected <I, O> O visit(Visitor<I, O> visitor, I input) {
            return visitor.food(this, input);
        }

        public static Food of(Id<de.njsm.stocks.client.business.entities.Food> food) {
            return new AutoValue_EventKeyHint_Food(IdImpl.from(food));
        }
    }

    @AutoValue
    public static abstract class User extends EventKeyHint {
        public abstract IdImpl<de.njsm.stocks.client.business.entities.User> id();

        @Override
        protected <I, O> O visit(Visitor<I, O> visitor, I input) {
            return visitor.user(this, input);
        }

        public static User of(Id<de.njsm.stocks.client.business.entities.User> user) {
            return new AutoValue_EventKeyHint_User(IdImpl.from(user));
        }
    }

    @AutoValue
    public static abstract class UserDevice extends EventKeyHint {
        public abstract IdImpl<de.njsm.stocks.client.business.entities.UserDevice> id();

        @Override
        protected <I, O> O visit(Visitor<I, O> visitor, I input) {
            return visitor.userDevice(this, input);
        }

        public static UserDevice of(Id<de.njsm.stocks.client.business.entities.UserDevice> userDevice) {
            return new AutoValue_EventKeyHint_UserDevice(IdImpl.from(userDevice));
        }
    }

    public static final class None extends EventKeyHint {
        @Override
        protected <I, O> O visit(Visitor<I, O> visitor, I input) {
            return visitor.none(this, input);
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof None;
        }
    }

    public interface Visitor<I, O> {
        default O visit(EventKeyHint hint, I input) {
            return hint.visit(this, input);
        }
        default O none(None none, I input) {
            throw new UnsupportedOperationException("unimplemented");
        }
        default O location(Location location, I input) {
            throw new UnsupportedOperationException("unimplemented");
        }
        default O food(Food food, I input) {
            throw new UnsupportedOperationException("unimplemented");
        }
        default O user(User user, I input) {
            throw new UnsupportedOperationException("unimplemented");
        }
        default O userDevice(UserDevice userDevice, I input) {
            throw new UnsupportedOperationException("unimplemented");
        }
    }

    protected abstract <I, O> O visit(Visitor<I, O> visitor, I input);

}
