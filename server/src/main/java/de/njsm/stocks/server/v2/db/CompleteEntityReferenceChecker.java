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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Identifiable;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Set;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;

/**
 * Given a set of foreign Versionables f referring to a primary Identifiable p
 * determine, whether f contains exactly all Versionables referring to p with
 * the correct version.
 */
public class CompleteEntityReferenceChecker<T extends Entity<T>, U extends Entity<U>, R extends Record> {

    private final Field<Integer> id;

    private final Field<Integer> version;

    private final Field<OffsetDateTime> validTimeStart;

    private final Field<OffsetDateTime> validTimeEnd;

    private final Field<OffsetDateTime> transactionTimeEnd;

    private final Field<Integer> referringField;

    private final Table<R> table;

    public CompleteEntityReferenceChecker(Field<Integer> id, Field<Integer> version, Field<OffsetDateTime> validTimeStart, Field<OffsetDateTime> validTimeEnd, Field<OffsetDateTime> transactionTimeEnd, Field<Integer> referringField, Table<R> table) {
        this.id = id;
        this.version = version;
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeEnd = transactionTimeEnd;
        this.referringField = referringField;
        this.table = table;
    }

    public StatusCode check(DSLContext context, Identifiable<T> recipe, Set<? extends Versionable<U>> ingredients) {
        Condition isCurrentlyValid = validTimeStart.le(DSL.currentOffsetDateTime())
                .and(DSL.currentOffsetDateTime().lt(validTimeEnd))
                .and(transactionTimeEnd.eq(INFINITY));

        String alias = "input_ingredients";
        Table<Record2<Integer, Integer>> inputIngredients;
        if (ingredients.isEmpty()) {
            // create empty table with matching column names
            inputIngredients = context.selectFrom(DSL.values(DSL.row(1, 1)))
                    .asTable(alias, id.getName(), version.getName())
                    .where(DSL.inline(1).eq(0));
        } else {
            Row2<Integer, Integer>[] ingredientsAsRows = new Row2[ingredients.size()];
            int i = 0;
            for (Versionable<U> ingredient : ingredients) {
                ingredientsAsRows[i] = DSL.row(ingredient.id(), ingredient.version());
                i++;
            }
            inputIngredients = context.selectFrom(DSL.values(ingredientsAsRows))
                    .asTable(alias, id.getName(), version.getName());
        }

        SelectConditionStep<Record2<Integer, Integer>> actualIngredients = context.select(id, version).from(table)
                .where(isCurrentlyValid.and(referringField.eq(recipe.id())));

        int numberOfDifferentIngredients =
                context.select(DSL.count(DSL.inline(1)))
                        .from(inputIngredients)
                        .fullOuterJoin(actualIngredients)
                        .using(id, version)
                        .where(inputIngredients.field(id).isNull()
                                .or(actualIngredients.field(id).isNull()))
                        .stream()
                        .findFirst()
                        .map(Record1::component1)
                        .orElseThrow();

        if (numberOfDifferentIngredients > 0) {
            return StatusCode.INVALID_DATA_VERSION;
        } else {
            return StatusCode.SUCCESS;
        }

    }
}
