package de.njsm.stocks.android.business.data.activity;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

import java.util.function.IntFunction;

public class DeletedUnitEvent extends DeletedEntityEvent<Unit> implements UnitIconResourceProvider {

    public DeletedUnitEvent(User initiatorUser, UserDevice initiatorUserDevice, Unit unit) {
        super(initiatorUser, initiatorUserDevice, unit);
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        String template = stringResourceResolver.apply(R.string.event_unit_deleted);
        return String.format(template,
                initiatorUser.name,
                entity.getName(),
                entity.getAbbreviation());
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.deletedUnitEvent(arg);
    }
}
