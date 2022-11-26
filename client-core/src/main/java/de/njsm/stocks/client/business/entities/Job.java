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

@AutoValue
public abstract class Job {

    public static Job create(Type name, Runnable runnable) {
        return new AutoValue_Job(name, runnable);
    }

    public abstract Type name();

    public abstract Runnable runnable();

    @Override
    public String toString() {
        return name().toString();
    }

    public enum Type {
        SETUP {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.setup(this, input);
            }
        },

        DATABASE {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.database(this, input);
            }
        },

        SYNCHRONISATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.synchronisation(this, input);
            }
        },

        ADD_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addLocation(this, input);
            }
        },

        DELETE_ERROR {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteError(this, input);
            }
        },

        DELETE_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteLocation(this, input);
            }
        },

        EDIT_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editLocation(this, input);
            }
        },

        ADD_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addUnit(this, input);
            }
        },

        DELETE_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteUnit(this, input);
            }
        },
        EDIT_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editUnit(this, input);
            }
        },
        ADD_SCALED_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addScaledUnit(this, input);
            }
        },
        EDIT_SCALED_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editScaledUnit(this, input);
            }
        },
        DELETE_SCALED_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteScaledUnit(this, input);
            }
        },
        ADD_FOOD {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addFood(this, input);
            }
        },

        DELETE_FOOD {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteFood(this, input);
            }
        },

        EDIT_FOOD {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editFood(this, input);
            }
        },

        ADD_FOOD_ITEM {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addFoodItem(this, input);
            }
        },

        DELETE_FOOD_ITEM {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteFoodItem(this, input);
            }
        },

        EDIT_FOOD_ITEM {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editFoodItem(this, input);
            }
        },

        GET_ACCOUNT_INFORMATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.getAccountInformation(this, input);
            }
        },

        ADD_EAN_NUMBER {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addEanNumber(this, input);
            }
        },

        DELETE_EAN_NUMBER {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteEanNumber(this, input);
            }
        },

        DELETE_USER_DEVICE {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteUserDevice(this, input);
            }
        },

        DELETE_USER {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteUser(this, input);
            }
        },

        ADD_RECIPE {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addRecipe(this, input);
            }
        },

        GET_SETTINGS {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.getSettings(this, input);
            }
        },

        UNKNOWN {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.unknown(this, input);
            }
        },

        SAVE_SETTINGS {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.saveSettings(this, input);
            }
        },

        CRASH_LOG_HANDLING {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.crashLogHandling(this, input);
            }
        };

        public abstract <I, O> O accept(TypeVisitor<I, O> visitor, I input);
    }

    public interface TypeVisitor<I, O> {

        default O visit(Type type, I input) {
            return type.accept(this, input);
        }

        O setup(Type type, I input);

        O database(Type type, I input);

        O synchronisation(Type type, I input);

        O addLocation(Type type, I input);

        O deleteError(Type type, I input);

        O deleteLocation(Type type, I input);

        O editLocation(Type type, I input);

        O unknown(Type type, I input);

        O addUnit(Type type, I input);

        O deleteUnit(Type type, I input);

        O editUnit(Type type, I input);

        O addScaledUnit(Type type, I input);

        O editScaledUnit(Type type, I input);

        O deleteScaledUnit(Type type, I input);

        O addFood(Type type, I input);

        O deleteFood(Type type, I input);

        O editFood(Type type, I input);

        O addFoodItem(Type type, I input);

        O deleteFoodItem(Type type, I input);

        O editFoodItem(Type type, I input);

        O getAccountInformation(Type type, I input);

        O addEanNumber(Type type, I input);

        O deleteEanNumber(Type type, I input);

        O deleteUserDevice(Type type, I input);

        O deleteUser(Type type, I input);

        O addRecipe(Type type, I input);

        O getSettings(Type type, I input);

        O saveSettings(Type type, I input);

        O crashLogHandling(Type type, I input);
    }

    public interface DefaultTypeVisitor<I, O> extends TypeVisitor<I, O> {

        O defaultImpl(Type type, I input);

        @Override
        default O setup(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O database(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O synchronisation(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addLocation(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteError(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteLocation(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O editLocation(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O unknown(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O editUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addScaledUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O editScaledUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteScaledUnit(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addFood(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteFood(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O editFood(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addFoodItem(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteFoodItem(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O editFoodItem(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O getAccountInformation(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addEanNumber(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteEanNumber(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteUserDevice(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O deleteUser(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default O addRecipe(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default  O getSettings(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default  O saveSettings(Type type, I input) {
            return defaultImpl(type, input);
        }

        @Override
        default  O crashLogHandling(Type type, I input) {
            return defaultImpl(type, input);
        }
    }
}
