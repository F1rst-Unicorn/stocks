package de.njsm.stocks.client.init.upgrade;

import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

import static de.njsm.stocks.client.init.upgrade.Version.V_2_0_3;
import static de.njsm.stocks.client.init.upgrade.Version.V_3_0_0;

public class EntityVersioningUpgradeProcedure extends SqlUpgradeProcedure {


    public EntityVersioningUpgradeProcedure(DatabaseManager dbManager) {
        super(dbManager, V_2_0_3, V_3_0_0);
    }

    @Override
    protected List<String> getUpgradeSqlScript() {
        List<String> result = new ArrayList<>();
        result.add("ALTER TABLE Food ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE User ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE Location ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE User_device ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        result.add("ALTER TABLE Food_item ADD COLUMN `version` INT NOT NULL DEFAULT 0");
        return result;
    }

    @Override
    protected List<String> getDowngradeSqlScript() {
        List<String> result = new ArrayList<>();
        result.add("ALTER TABLE Food DROP COLUMN `version`");
        result.add("ALTER TABLE User DROP COLUMN `version`");
        result.add("ALTER TABLE Location DROP COLUMN `version`");
        result.add("ALTER TABLE User_device DROP COLUMN `version`");
        result.add("ALTER TABLE Food_item DROP COLUMN `version`");
        return result;
    }
}
