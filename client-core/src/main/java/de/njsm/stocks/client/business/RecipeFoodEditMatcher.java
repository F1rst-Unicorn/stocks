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

package de.njsm.stocks.client.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RecipeFoodEditMatcher<Present, ToAdd, ToDelete, ToEdit, ToEditNetwork> {

    public static final int FRESHLY_CREATED_ENTITY_ID = -1;

    private final List<Present> present;

    private final List<ToEdit> form;

    private List<ToAdd> toAdd;

    private List<ToDelete> toDelete;

    private final List<ToEditNetwork> toEdit;

    private boolean computed = false;

    RecipeFoodEditMatcher(List<Present> present, List<ToEdit> form) {
        this.present = new ArrayList<>(present);
        this.form = new ArrayList<>(form);
        toEdit = new ArrayList<>();
    }

    private void compute() {
        if (computed)
            return;
        computed = true;
        matchItemsOfRating(Rating.PERFECT);
        matchItemsOfRating(Rating.EQUAL);
        matchItemsOfRating(Rating.AMOUNT_CHANGED);
        matchItemsOfRating(Rating.UNIT_CHANGED);
        matchItemsOfRating(Rating.UNRELATED);
        toAdd = this.form.stream()
                .map(this::createToAdd)
                .collect(Collectors.toList());
        toDelete = this.present.stream()
                .map(this::createToDelete)
                .collect(Collectors.toList());
    }

    abstract ToAdd createToAdd(ToEdit form);
    abstract ToDelete createToDelete(Present present);
    abstract ToEditNetwork createToEdit(Present present, ToEdit form);

    private void matchItemsOfRating(Rating expectedRating) {
        for (var formIterator = form.iterator(); formIterator.hasNext();) {
            var formItem = formIterator.next();
            for (var presentIterator = present.iterator(); presentIterator.hasNext();) {
                var presentItem = presentIterator.next();
                var rating = rate(presentItem, formItem);
                if (rating == expectedRating) {
                    toEdit.add(createToEdit(presentItem, formItem));
                    presentIterator.remove();
                    formIterator.remove();
                    break;
                }
            }
        }
    }

    List<ToAdd> getToAdd() {
        compute();
        return toAdd;
    }

    List<ToDelete> getToDelete() {
        compute();
        return toDelete;
    }

    List<ToEditNetwork> getToEdit() {
        compute();
        return toEdit;
    }

    abstract Rating rate(Present present, ToEdit form);

    enum Rating {
        UNRELATED,
        UNIT_CHANGED,
        AMOUNT_CHANGED,
        EQUAL,
        PERFECT,
    }
}
