package com.sun.alwayssunny.Service;

import android.provider.BaseColumns;

/**
 * Created by brett on 5/7/2016.
 */
public final class DB_LocationContract {

    public DB_LocationContract() {}

    public static abstract class LocationDB {
        public static final String DATABASE_NAME = "db_locations";
        public static final Integer DATABASE_VERSION = 1;
    }

    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "long";
        public static final String _ID = "1";
    }
}
