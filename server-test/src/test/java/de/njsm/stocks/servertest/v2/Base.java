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

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.client.business.UpdateService;
import de.njsm.stocks.servertest.DaggerRootComponent;
import de.njsm.stocks.servertest.RootComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import javax.inject.Inject;
import java.lang.reflect.Method;

public class Base {

    UpdateService updateService;

    TestInfo testInfo;

    RootComponent dagger;

    private int counter = 0;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        dagger = DaggerRootComponent.builder()
                .withTestInfo(testInfo)
                .build();
    }

    @Deprecated
    String getUniqueName(String distinguisher) {
        return this.getClass().getCanonicalName() + "." + distinguisher + counter++;
    }

    String getUniqueName() {
        return (testInfo.getTestClass().map(Class::getName).orElse("dummy")
                + "-" + testInfo.getTestMethod().map(Method::getName).orElse("dummy")
                + counter++).replace('.', '-');
    }

    @Inject
    void setUpdateService(UpdateService updateService) {
        this.updateService = updateService;
    }

    @Inject
    void setTestInfo(TestInfo testInfo) {
        this.testInfo = testInfo;
    }
}
