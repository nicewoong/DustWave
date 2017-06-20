package kr.ac.knu.dustwave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by hojun on 2015-08-22.
 */
public class LocalDatabase {

    // DB Name, DB Table Name, DB Version Information
    // =========================================
    public static final String DB_NAME = "dustwave.db";
    protected static final int DB_VERSION = 1;
    public static final String TABLE_BUS_STOP = "BUS_STOP";
    public static final String TABLE_DUST_INFO = "DUST_INFO";


    // =========================================

    Context mContext = null;
    private static LocalDatabase mDatabase = null;
    private static final String TAG = "LocalDatabase";
    private SQLiteDatabase mDb;
    private DatabaseHelper dbHelper;

    // =========================================
    public static final String DB_KEY_ISNEW = "isNew";


    //Singleton Appearance
    public static LocalDatabase getInstance(Context context) {
        if (mDatabase == null) {
            mDatabase = new LocalDatabase(context);
        }
        return mDatabase;
    }

    //Constructor
    private LocalDatabase(Context context) {
        mContext = context;
    }

    public boolean writableOpen() {
        try {
            if (dbHelper == null) {
                dbHelper = new DatabaseHelper(mContext);
                mDb = dbHelper.getWritableDatabase();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean readableOpen() {
        try {
            if (dbHelper == null) {
                dbHelper = new DatabaseHelper(mContext);
                mDb = dbHelper.getReadableDatabase();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void close() {
        if (mDb != null) {
            mDb.close();
            mDatabase = null;
        }
    }


    public Cursor rawQuery(String SQL) {
        Cursor c1 = null;
        try {
            c1 = mDb.rawQuery(SQL, null);
            return c1;
        } catch (Exception ex) {
            Log.e(TAG, "Raw Query Error : " + ex.getMessage());
            return null;
        }

    }


    public boolean execSQL(String SQL) {
        try {
            mDb.execSQL(SQL);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }


    public void insertSQL(String table, ContentValues values) {
        try {
            mDb.insert(table, null, values);
        } catch (Exception ex) {
            ;
        }
    }

    public void updateSQL(String table, ContentValues values, String whereStr) {
        try {
            mDb.update(table, values, whereStr, null);
        } catch (Exception ex) {
            ;
        }
    }

    // ===================================

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /**
             *             버스 정류소 Table
             */
            String SQL = "create table if not exists " + TABLE_BUS_STOP +
                    "(" +
                    LocalDatabaseKey.bus_stop_id + " INTEGER NOT NULL PRIMARY KEY, " + // Primary Key , not null //11
                    LocalDatabaseKey.bus_stop_name + " TEXT, " +
                    LocalDatabaseKey.bus_stop_latitude + " REAL, " +
                    LocalDatabaseKey.bus_stop_longitude + " REAL, " +
                    ")";
            db.execSQL(SQL);


            /**
             *              미세먼지정보 TABLE
             */
            SQL = "create table if not exists " + TABLE_DUST_INFO +
                    "( " +
                    LocalDatabaseKey.bus_stop_id + " INTEGER NOT NULL PRIMARY KEY, " +
                    LocalDatabaseKey.dust_info_pm10 + " TEXT, " +
                    LocalDatabaseKey.dust_info_pm25 + " TEXT, " +
                    LocalDatabaseKey.dust_info_pm10_predict + " TEXT, " +
                    LocalDatabaseKey.dust_info_pm25_predict + " TEXT, " +
                    LocalDatabaseKey.dust_info_temperature + " INTEGER, " +
                    LocalDatabaseKey.dust_info_wind_speed + " TEXT, " +
                    LocalDatabaseKey.dust_info_wind_direction + " TEXT, " +
                    LocalDatabaseKey.dust_info_humidity + " INTEGER, " +
                    LocalDatabaseKey.dust_info_weather + " TEXT, " +
                    LocalDatabaseKey.dust_info_time + " DATETIME, " +
                    ")";
            db.execSQL(SQL);



            /**
             *  Insert dummy value
             */
            ContentValues addRowValues = new ContentValues();
            addRowValues.put(LocalDatabaseKey.bus_stop_id, "1");
            db.insert(TABLE_BUS_STOP, null, addRowValues);
            addRowValues.clear();

            addRowValues.put(LocalDatabaseKey.bus_stop_id, "1");
            db.insert(TABLE_DUST_INFO, null, addRowValues);
            addRowValues.clear();

            //And Delete
            db.execSQL("delete from " + TABLE_BUS_STOP);
            db.execSQL("delete from " + TABLE_DUST_INFO);

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    }// End of Inner Class "Database Helper"

}
