package de.njsm.stocks.client.init.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeRegistry {

    private ArrayList<Upgrader> upgraders;

    public UpgradeRegistry(List<Upgrader> upgraders) {
        this.upgraders = new ArrayList<>(upgraders);
    }

    public List<Upgrader> getUpgraders(Version current, Version target) {
        return upgraders.stream()
                .filter(upgrader -> current.compareTo(upgrader.getBaseVersion()) <= 0)
                .filter(upgrader -> upgrader.getTargetVersion().compareTo(target) <= 0)
                .collect(Collectors.toList());
    }
}
