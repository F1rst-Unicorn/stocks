package de.njsm.stocks.backend.db.data;


public class SqlEanNumberTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_NUMBER;
    public static final String COL_FOOD;

    public static final String CREATE;
    public static final String DROP;
    public static final String SELECT_NUMBER;

    static {
        NAME = "EAN_number";
        COL_ID = "_id";
        COL_NUMBER = "number";
        COL_FOOD = "identifies";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_NUMBER + " varchar(13) NOT NULL,\n" +
                "    " + COL_FOOD + " int UNSIGNED NOT NULL,\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                "    CONSTRAINT `number_identifies_food` FOREIGN KEY (" + COL_FOOD +
                ") REFERENCES " + SqlFoodTable.NAME + "(" + SqlFoodTable.COL_ID +
                ") ON DELETE RESTRICT ON UPDATE CASCADE\n" +
                ")";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        SELECT_NUMBER = "SELECT * FROM " + NAME + " WHERE " + COL_FOOD + "=?";
    }
}
