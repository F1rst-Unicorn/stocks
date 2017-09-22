package de.njsm.stocks.backend.db.data;

public class SqlFoodItemTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_EAT_BY;
    public static final String COL_OF_TYPE;
    public static final String COL_STORED_IN;
    public static final String COL_REGISTERS;
    public static final String COL_BUYS;

    public static final String CREATE;
    public static final String DROP;
    public static final String SELECT_ALL;
    public static final String SELECT_AGGREGATED_MIN_DATE_LOC;
    public static final String SELECT_FOOD_TYPE_ALL;
    public static final String SELECT_FOOD_EMPTY;
    public static final String SELECT_FOOD_EAT_SOON;
    public static final String SELECT_MAX_LOCATION;
    public static final String SEARCH_FOOD;

    static {
        NAME = "Food_item";
        COL_ID = "_id";
        COL_EAT_BY = "eat_by";
        COL_OF_TYPE = "of_type";
        COL_STORED_IN = "stored_in";
        COL_REGISTERS = "registers";
        COL_BUYS = "buys";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_EAT_BY + " varchar(19) NOT NULL,\n" +
                "    " + COL_OF_TYPE + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_REGISTERS + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_BUYS + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_STORED_IN + " int UNSIGNED NOT NULL,    \n" +
                "    FOREIGN KEY (" + COL_OF_TYPE + ") REFERENCES " + SqlFoodTable.NAME + "(" + SqlFoodTable.COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_REGISTERS + ") REFERENCES " + SqlDeviceTable.NAME + "(" + SqlDeviceTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_BUYS + ") REFERENCES " + SqlUserTable.NAME + "(" + SqlUserTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_STORED_IN + ") REFERENCES " + SqlLocationTable.NAME + "(" + SqlLocationTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                ");";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        SELECT_ALL = "SELECT * FROM " + NAME;
        SELECT_AGGREGATED_MIN_DATE_LOC =
                "SELECT i._id, f._id as food_id, f.name as name ,count(*) as amount ,i." + COL_EAT_BY + " as date " +
                "FROM Food f, " + NAME + " i " +
                "WHERE i." + COL_STORED_IN + "=? and f._id=i." + COL_OF_TYPE + " " +
                "GROUP BY name " +
                "HAVING i." + COL_EAT_BY + " = MIN(i." + COL_EAT_BY + ")" +
                "ORDER BY date ASC;";

        SELECT_FOOD_TYPE_ALL =
                "SELECT i._id, f.name as food, i." + COL_EAT_BY + " as date, u.name as user, d.name as device, l.name as location " +
                "FROM Food f, " + NAME + " i, User u, User_device d, Location l " +
                "WHERE f._id = ? AND " +
                        "i." + COL_OF_TYPE + " = f._id AND " +
                        "i." + COL_BUYS + " = u._id AND " +
                        "i." + COL_REGISTERS + " = d._id AND " +
                        "i." + COL_STORED_IN + " = l._id " +
                "ORDER BY date ASC";
        SELECT_FOOD_EMPTY =
                "SELECT f._id, f.name as name " +
                "FROM Food f " +
                "WHERE f._id NOT IN (SELECT DISTINCT i.of_type " +
                                    "FROM Food_item i)";
        SELECT_FOOD_EAT_SOON =
                "SELECT i._id, f._id as food_id, f.name as name ,count(*) as amount ,i." + COL_EAT_BY + " as date " +
                        "FROM Food f, " + NAME + " i " +
                        "WHERE f._id=i." + COL_OF_TYPE + " " +
                        "GROUP BY name " +
                        "HAVING i." + COL_EAT_BY + " = MIN(i." + COL_EAT_BY + ")" +
                        "ORDER BY date ASC;";
        SEARCH_FOOD =
                "SELECT f._id as _id, f.name as name ,count(*) as amount ,i." + COL_EAT_BY + " as date " +
                        "FROM Food f, " + NAME + " i " +
                        "WHERE f._id=i." + COL_OF_TYPE + " and f.name like ? " +
                        "GROUP BY name " +
                        "HAVING i." + COL_EAT_BY + " = MIN(i." + COL_EAT_BY + ") " +
                "UNION " +
                "SELECT f._id as _id, f.name as name, 0 as amount, '0000-00-00 00:00:00' as date " +
                        "FROM Food f " +
                        "WHERE f.name like ? and f._id NOT IN (SELECT DISTINCT i.of_type " +
                                                              "FROM Food_item i);";
        SELECT_MAX_LOCATION =
                "SELECT l._id, l.name, count(*) as c " +
                "FROM Food_item i, Location l " +
                "WHERE i.of_type=? AND i.stored_in=l._id " +
                "GROUP BY l._id " +
                "ORDER BY c DESC LIMIT 1";
    }
}
