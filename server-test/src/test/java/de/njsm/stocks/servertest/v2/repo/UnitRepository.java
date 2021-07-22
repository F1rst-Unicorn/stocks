package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.servertest.v2.UnitTest;

import java.util.List;

public class UnitRepository {


    public static int getAnyUnitId() {
        List<Integer> ids = UnitTest.assertOnData()
                .extract()
                .jsonPath()
                .getList("data.id");

        if (ids.isEmpty())
            return UnitTest.createNew("getAnyUnitId", "getAnyUnitId");
        else
            return ids.get(0);
    }
}
