package de.njsm.stocks.android.business.data.activity;

import de.njsm.stocks.android.business.data.activity.differ.UnitAbbreviationDiffer;
import de.njsm.stocks.android.business.data.activity.differ.UnitNameDiffer;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class ChangedUnitEvent extends ChangedEntityEvent<Unit> implements UnitIconResourceProvider{


    public ChangedUnitEvent(User initiatorUser, UserDevice initiatorUserDevice, Unit version1, Unit version2) {
        super(initiatorUser, initiatorUserDevice, version1, version2);
    }

    @Override
    protected List<PartialDiffGenerator<Unit>> getDiffers(IntFunction<String> stringResourceResolver, SentenceObject object) {
        List<PartialDiffGenerator<Unit>> list = new ArrayList<>();
        list.add(new UnitNameDiffer(stringResourceResolver, oldEntity, newEntity, object));
        list.add(new UnitAbbreviationDiffer(stringResourceResolver, oldEntity, newEntity, object));
        return list;
    }

    @Override
    protected String getExplicitObject() {
        return oldEntity.getName();
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedUnitEvent(arg);
    }
}
