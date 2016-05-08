package com.sun.alwayssunny.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.sun.alwayssunny.Classes.WeatherStation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brett on 5/7/2016.
 */
public class DB_LocationHelper extends SQLiteOpenHelper {
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_REAL = " REAL";
    private static final String SQL_CREATE_TABLE =
            ("CREATE TABLE " + DB_LocationContract.LocationEntry.TABLE_NAME + "(" +
                    DB_LocationContract.LocationEntry._ID + " INTEGER PRIMARY KEY , " +
                    DB_LocationContract.LocationEntry.COLUMN_CITY + TYPE_TEXT + "UNIQUE, " +
                    DB_LocationContract.LocationEntry.COLUMN_LAT + TYPE_REAL + ", " +
                    DB_LocationContract.LocationEntry.COLUMN_LONG + TYPE_REAL + ")");

    public DB_LocationHelper (Context context) {
        super(context, DB_LocationContract.LocationDB.DATABASE_NAME,
              null, DB_LocationContract.LocationDB.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*
     * insertLocation: Insert a single location value into the location table.
     * params: city - the place that the user is visiting
     *         lat - the latitude of the place
     *         lng - the longitude of the place
     * returns: true if successful, otherwise false.
     */
    public boolean insertLocation(String city, Double lat, Double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DB_LocationContract.LocationEntry.COLUMN_CITY, city);
        contentValues.put(DB_LocationContract.LocationEntry.COLUMN_LAT, lat);
        contentValues.put(DB_LocationContract.LocationEntry.COLUMN_LONG, lng);

        Long newColumnID =
                db.insert(DB_LocationContract.LocationEntry.TABLE_NAME, null, contentValues);

        if (newColumnID == -1) {
            return false;
        }
        return true;
    }

    /*
     * selectLatestLocations: retrieves the latest locations the user has visited.
     * params: numStations - the number of locations to return.
     * returns: A list of WeatherStations containing location data.
     */
    public List<WeatherStation> selectLatestLocations(Integer numStations) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<WeatherStation> stations = new ArrayList<WeatherStation>();
        WeatherStation station;

        // List of projections desired from the query
        String[] projection = {
                DB_LocationContract.LocationEntry._ID,
                DB_LocationContract.LocationEntry.COLUMN_CITY,
                DB_LocationContract.LocationEntry.COLUMN_LAT,
                DB_LocationContract.LocationEntry.COLUMN_LONG
        };

        // How we want to order the data
        String sortOrder = DB_LocationContract.LocationEntry._ID + " DESC";

        // Run the DB query
        Cursor c = db.query(
                DB_LocationContract.LocationEntry.TABLE_NAME,   // The table to query
                projection,                                     // The columns to return
                null,                                           // The columns for the WHERE clause
                null,                                           // The values for the WHERE clause
                null,                                           // don't group the rows
                null,                                           // don't filter by row groups
                sortOrder,                                      // The sort order
                String.valueOf(numStations)                     // Limit the number of stations
        );

        // Iterate through the query, place into list of objects.
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            station = new WeatherStation();
            station.stationName = c.getString(
                    c.getColumnIndexOrThrow(DB_LocationContract.LocationEntry.COLUMN_CITY));
            station.latitude = c.getDouble(
                    c.getColumnIndexOrThrow(DB_LocationContract.LocationEntry.COLUMN_LAT));
            station.longitude = c.getDouble(
                    c.getColumnIndexOrThrow(DB_LocationContract.LocationEntry.COLUMN_LONG));

            stations.add(station);
        }

        return stations;
    }
}
