package de.njsm.stocks.common.api.visitor;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.impl.*;

public interface InsertableVisitor<I, O> {

    default <T extends Entity<T>> O visit(Insertable<T> insertable, I argument) {
        return insertable.accept(this, argument);
    }

    O eanNumberForInsertion(EanNumberForInsertion eanNumberForInsertion, I argument);

    O foodForInsertion(FoodForInsertion foodForInsertion, I argument);

    O foodItemForInsertion(FoodItemForInsertion foodItemForInsertion, I argument);

    O locationForInsertion(LocationForInsertion locationForInsertion, I argument);

    O recipeForInsertion(RecipeForInsertion recipeForInsertion, I argument);

    O scaledUnitForInsertion(ScaledUnitForInsertion scaledUnitForInsertion, I argument);

    O unitForInsertion(UnitForInsertion unitForInsertion, I argument);

    O userDeviceForInsertion(UserDeviceForInsertion userDeviceForInsertion, I argument);

    O userForInsertion(UserForInsertion userForInsertion, I argument);

    O recipeIngredientWithIdForInsertion(RecipeIngredientWithIdForInsertion recipeIngredientWithIdForInsertion, I argument);

    O recipeProductWithIdForInsertion(RecipeProductWithIdForInsertion recipeProductWithIdForInsertion, I argument);
}
