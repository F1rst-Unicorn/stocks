package de.njsm.stocks.backend.data;

import android.content.ContentValues;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.db.data.*;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.visitor.StocksDataVisitorImpl;

public class SerialisationVisitor extends StocksDataVisitorImpl<Integer, ContentValues> {


    @Override
    public ContentValues update(Update update, Integer index) {
        ContentValues result = new ContentValues();
        result.put(SqlUpdateTable.COL_ID, index);
        result.put(SqlUpdateTable.COL_NAME, update.table);
        result.put(SqlUpdateTable.COL_DATE, Config.DATABASE_DATE_FORMAT.format(update.lastUpdate));
        return result;
    }

    @Override
    public ContentValues userDevice(UserDevice device, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlDeviceTable.COL_ID, device.id);
        result.put(SqlDeviceTable.COL_NAME, device.name);
        result.put(SqlDeviceTable.COL_USER, device.userId);
        return result;
    }

    @Override
    public ContentValues user(User user, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlUserTable.COL_ID, user.id);
        result.put(SqlUserTable.COL_NAME, user.name);
        return result;
    }

    @Override
    public ContentValues location(Location location, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlLocationTable.COL_ID, location.id);
        result.put(SqlLocationTable.COL_NAME, location.name);
        return result;
    }

    @Override
    public ContentValues foodItem(FoodItem item, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlFoodItemTable.COL_ID, item.id);
        result.put(SqlFoodItemTable.COL_REGISTERS, item.registers);
        result.put(SqlFoodItemTable.COL_BUYS, item.buys);
        result.put(SqlFoodItemTable.COL_OF_TYPE, item.ofType);
        result.put(SqlFoodItemTable.COL_STORED_IN, item.storedIn);
        result.put(SqlFoodItemTable.COL_EAT_BY, Config.DATABASE_DATE_FORMAT.format(item.eatByDate));
        return result;
    }

    @Override
    public ContentValues food(Food food, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlFoodTable.COL_ID, food.id);
        result.put(SqlFoodTable.COL_NAME, food.name);
        return result;
    }

    @Override
    public ContentValues eanNumber(EanNumber number, Integer input) {
        ContentValues result = new ContentValues();
        result.put(SqlEanNumberTable.COL_ID, number.id);
        result.put(SqlEanNumberTable.COL_NUMBER, number.eanCode);
        result.put(SqlEanNumberTable.COL_FOOD, number.identifiesFood);
        return result;
    }
}
