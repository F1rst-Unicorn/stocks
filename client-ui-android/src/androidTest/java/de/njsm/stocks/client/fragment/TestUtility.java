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

package de.njsm.stocks.client.fragment;

import androidx.annotation.IdRes;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.client.business.entities.conflict.ConflictData;
import de.njsm.stocks.client.ui.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

public interface TestUtility {

    default void checkTextField(@IdRes int fieldId, ConflictData<String> text) {
        checkFieldContent(fieldId, text.suggestedValue());
        checkConflictFields(fieldId, text);
    }

    default void checkMergingTextField(@IdRes int fieldId, ConflictData<String> mergedText) {
        String mergedDescription = String.format(mergedText.suggestedValue(),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_original),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_remote),
                InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext().getString(R.string.hint_local)
        );
        checkFieldContent(fieldId, mergedDescription);
    }

    default void checkSpinner(@IdRes int fieldId, String selected, ConflictData<String> text) {
        onView(withId(fieldId)).check(matches(allOf(
                isDisplayed(),
                hasDescendant(withText(selected))
        )));
        checkConflictFields(fieldId, text);
    }

    default void checkConflictFields(int fieldId, ConflictData<String> text) {
        onView(allOf(isDescendantOfA(withId(fieldId)), withId(R.id.conflict_labels_original_content))).check(matches(withText(text.original())));
        onView(allOf(isDescendantOfA(withId(fieldId)), withId(R.id.conflict_labels_remote_content))).check(matches(withText(text.remote())));
        onView(allOf(isDescendantOfA(withId(fieldId)), withId(R.id.conflict_labels_local_content))).check(matches(withText(text.local())));
    }

    static void checkFieldContent(int fieldId, String text) {
        onView(allOf(
                isDescendantOfA(withId(fieldId)),
                withClassName(is(TextInputEditText.class.getName()))
        )).check(matches(withText(text)));
    }


}
