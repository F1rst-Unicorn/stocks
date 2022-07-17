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

package de.njsm.stocks.client.database.util;

import junit.framework.AssertionFailedError;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomnessProvider implements TestRule {

    private static final String SPECIAL_CHARACTER = "%";

    private final Map<String, Integer> ids = new HashMap<>();

    private final Map<String, Integer> nameCounter = new HashMap<>();

    public int getId(String name, Integer... forbiddenValues) {
        if (name.contains(SPECIAL_CHARACTER)) {
            throw new IllegalArgumentException(name + " contains forbidden character '" + SPECIAL_CHARACTER + "'");
        }
        int result = getResult(forbiddenValues);
        return ids.merge(computeName(name), result, (a, b) -> result);
    }

    private String computeName(String name) {
        int counter = nameCounter.merge(name, 0, (presentValue, newValue) -> presentValue + 1);
        return name + SPECIAL_CHARACTER + counter;
    }

    private int getResult(Integer... forbiddenValues) {
        int result;
        Set<Integer> setOfForbiddenValues = new HashSet<>(Arrays.asList(forbiddenValues));
        setOfForbiddenValues.addAll(ids.values());

        do {
            result = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        } while(setOfForbiddenValues.contains(result));
        return result;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    AssertionFailedError randomValues = new AssertionFailedError(renderRandomValues());
                    randomValues.setStackTrace(new StackTraceElement[0]);
                    e.addSuppressed(randomValues);
                    throw e;
                }
            }
        };
    }

    private String renderRandomValues() {
        StringBuilder builder = new StringBuilder();
        builder.append("ids used:\n");
        renderMap(builder, ids);
        return builder.toString();
    }

    private void renderMap(StringBuilder builder, Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            builder.append("        ");
            builder.append(entry.getKey());
            builder.append(": ");
            builder.append(entry.getValue());
            builder.append("\n");
        }
    }
}
