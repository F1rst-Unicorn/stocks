package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Identifiable;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Set;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;

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
                ingredientsAsRows[i] = DSL.row(ingredient.getId(), ingredient.getVersion());
                i++;
            }
            inputIngredients = context.selectFrom(DSL.values(ingredientsAsRows))
                    .asTable(alias, id.getName(), version.getName());
        }

        SelectConditionStep<Record2<Integer, Integer>> actualIngredients = context.select(id, version).from(table)
                .where(isCurrentlyValid.and(referringField.eq(recipe.getId())));

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
