package de.njsm.stocks.client.init.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeRegistry {

    private ArrayList<UpgradeProcedure> upgradeProcedures;

    public UpgradeRegistry(List<UpgradeProcedure> upgradeProcedures) {
        this.upgradeProcedures = new ArrayList<>(upgradeProcedures);
    }

    public List<UpgradeProcedure> getUpgradeProcedures(Version current, Version target) {
        return upgradeProcedures.stream()
                .filter(upgradeProcedure -> current.compareTo(upgradeProcedure.getBaseVersion()) <= 0)
                .filter(upgradeProcedure -> upgradeProcedure.getTargetVersion().compareTo(target) <= 0)
                .collect(Collectors.toList());
    }
}
