package com.baset.carfinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baset.carfinder.BuildConfig;
import com.baset.carfinder.model.ModelParkHistory;

import java.util.ArrayList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABSE_NAME = BuildConfig.APPLICATION_ID+".db";
    public static final String CREATE_TABLE = "CREATE TABLE " + ModelParkHistory.TABLE_NAME + "("
            + ModelParkHistory.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ModelParkHistory.CAR_NAME + " TEXT,"
            + ModelParkHistory.CAR_COLOR + " TEXT,"
            + ModelParkHistory.CAR_PLAQUE + " TEXT,"
            + ModelParkHistory.CAR_DATE_PARK + " TEXT,"
            + ModelParkHistory.CAR_CLOCK_PARK + " TEXT,"
            + ModelParkHistory.CAR_PARK_LATITUDE + " REAL,"
            + ModelParkHistory.CAR_PARK_LONGITUDE + " REAL,"
            + ModelParkHistory.CAR_PARK_ADDRESS + " TEXT"
            + ")";

    public SqliteHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ModelParkHistory.TABLE_NAME);
        onCreate(db);
    }

    public long insertHistory(String carName, String carColor, String carPlaque, String datePark, String clockPark, String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelParkHistory.CAR_NAME, carName);
        values.put(ModelParkHistory.CAR_COLOR, carColor);
        values.put(ModelParkHistory.CAR_PLAQUE, carPlaque);
        values.put(ModelParkHistory.CAR_DATE_PARK, datePark);
        values.put(ModelParkHistory.CAR_CLOCK_PARK, clockPark);
        values.put(ModelParkHistory.CAR_PARK_ADDRESS, address);
        values.put(ModelParkHistory.CAR_PARK_LATITUDE, latitude);
        values.put(ModelParkHistory.CAR_PARK_LONGITUDE, longitude);
        long id = db.insert(ModelParkHistory.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<ModelParkHistory> getAllNotes() {
        List<ModelParkHistory> parkHistories = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ModelParkHistory.TABLE_NAME + " ORDER BY " +
                ModelParkHistory.CAR_DATE_PARK + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ModelParkHistory parkHistory = new ModelParkHistory();
                parkHistory.setId(cursor.getInt(cursor.getColumnIndex(ModelParkHistory.COLUMN_ID)));
                parkHistory.setCarName(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_NAME)));
                parkHistory.setCarColor(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_COLOR)));
                parkHistory.setCarPlaque(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_PLAQUE)));
                parkHistory.setDatePark(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_DATE_PARK)));
                parkHistory.setClockPark(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_CLOCK_PARK)));
                parkHistory.setAddress(cursor.getString(cursor.getColumnIndex(ModelParkHistory.CAR_PARK_ADDRESS)));
                parkHistory.setLatitude(cursor.getDouble(cursor.getColumnIndex(ModelParkHistory.CAR_PARK_LATITUDE)));
                parkHistory.setLongitude(cursor.getDouble(cursor.getColumnIndex(ModelParkHistory.CAR_PARK_LONGITUDE)));
                parkHistories.add(parkHistory);
            } while (cursor.moveToNext());
        }
        db.close();
        return parkHistories;
    }

    public int getHistoryCount() {
        String countquery = "SELECT * FROM " + ModelParkHistory.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countquery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deletHistory(ModelParkHistory parkHistory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ModelParkHistory.TABLE_NAME, ModelParkHistory.COLUMN_ID + " =? ", new String[]{String.valueOf(parkHistory.getId())});
    }

    public void deletAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ModelParkHistory.TABLE_NAME, null, null);
    }
}
