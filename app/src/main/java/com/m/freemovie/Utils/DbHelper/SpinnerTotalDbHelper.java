package com.m.freemovie.Utils.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpinnerTotalDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Spinner.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_SHOW_PAGES = "show_pages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SHOW_ID = "show_id";
    public static final String COLUMN_TOTAL_PAGES = "total_pages";
    public static final String COLUMN_LAST_SAVED_PAGE = "last_saved_page";

    // Create table SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_SHOW_PAGES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SHOW_ID + " TEXT NOT NULL, " +
                    COLUMN_TOTAL_PAGES + " INTEGER DEFAULT 0, " +
                    COLUMN_LAST_SAVED_PAGE + " INTEGER DEFAULT 1);";

    public SpinnerTotalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOW_PAGES);
        onCreate(db);
    }

    // Save or update show pages info
    public void saveOrUpdateShowPages(String showId, int totalPages, int currentPage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHOW_ID, showId);
        values.put(COLUMN_TOTAL_PAGES, totalPages);
        values.put(COLUMN_LAST_SAVED_PAGE, currentPage);

        // Check if show already exists
        Cursor cursor = db.query(TABLE_SHOW_PAGES,
                new String[]{COLUMN_SHOW_ID},
                COLUMN_SHOW_ID + " = ?",
                new String[]{showId},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            // Update existing record
            db.update(TABLE_SHOW_PAGES, values, COLUMN_SHOW_ID + " = ?", new String[]{showId});
        } else {
            // Insert new record
            db.insert(TABLE_SHOW_PAGES, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    // Get total pages for a show
    public int getTotalPages(String showId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalPages = 0;

        Cursor cursor = db.query(TABLE_SHOW_PAGES,
                new String[]{COLUMN_TOTAL_PAGES},
                COLUMN_SHOW_ID + " = ?",
                new String[]{showId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            totalPages = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PAGES));
            cursor.close();
        }

        db.close();
        return totalPages;
    }

}