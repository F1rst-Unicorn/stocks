package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;

public class AddStatementVisitor extends StocksDataVisitorImpl<Void, String> {

    public String visit(Data data) {
        return visit(data, null);
    }

    @Override
    public String food(Food food, Void input) {
        return "INSERT INTO Food (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String foodItem(FoodItem item, Void input) {
        return "INSERT INTO Food_item " +
                "(`ID`, eat_by, of_type, stored_in, registers, buys) VALUES (?,?,?,?,?,?)";
    }

    @Override
    public String user(User u, Void input) {
        return "INSERT INTO User (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String userDevice(UserDevice device, Void input) {
        return "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";
    }

    @Override
    public String location(Location location, Void input) {
        return "INSERT INTO Location (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String update(Update update, Void input) {
        return "UPDATE Updates SET last_update=? WHERE table_name=?";
    }

    @Override
    public String eanNumber(EanNumber number, Void input) {
        return "INSERT INTO EAN_number (`ID`, number, identifies) VALUES (?,?,?)";
    }
}
