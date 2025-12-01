package com.m.freemovie.Utils.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WatchHistoryDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WatchHistory.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table name and columns
    public static final String TABLE_WATCHED_EPISODES = "watched_episodes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SHOW_ID = "show_id";
    public static final String COLUMN_SHOW_TITLE = "show_title";
    public static final String COLUMN_SEASON_NUM = "season_num";
    public static final String COLUMN_EPISODE_NUM = "episode_num";
    public static final String COLUMN_WATCHED = "watched";
    public static final String COLUMN_WATCHED_TIME = "watched_time";
    public static final String COLUMN_WATCHED_SEASON_ID ="season_id";
    
    // Create table SQL
    private static final String CREATE_TABLE = 
        "CREATE TABLE " + TABLE_WATCHED_EPISODES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SHOW_ID + " TEXT NOT NULL, " +
            COLUMN_SHOW_TITLE + " TEXT, " +
            COLUMN_SEASON_NUM + " INTEGER NOT NULL, " +
            COLUMN_EPISODE_NUM + " INTEGER NOT NULL, " +
            COLUMN_WATCHED + " INTEGER DEFAULT 0, " +
            COLUMN_WATCHED_TIME + " INTEGER, " + COLUMN_WATCHED_SEASON_ID + " INTEGER, " +
            "UNIQUE(" + COLUMN_SHOW_ID + ", " + COLUMN_SEASON_NUM + ", " + COLUMN_EPISODE_NUM + ", " + COLUMN_WATCHED_SEASON_ID + ")" +
        ");";

    public WatchHistoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCHED_EPISODES);
        onCreate(db);
    }
    
    // Mark episode as watched
    public void markEpisodeAsWatched(String showId, String showTitle, int seasonNum, int episodeNum,String seasonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHOW_ID, showId);
        values.put(COLUMN_SHOW_TITLE, showTitle);
        values.put(COLUMN_SEASON_NUM, seasonNum);
        values.put(COLUMN_EPISODE_NUM, episodeNum);
        values.put(COLUMN_WATCHED, 1);
        values.put(COLUMN_WATCHED_TIME, System.currentTimeMillis());
        values.put(COLUMN_WATCHED_SEASON_ID,seasonId);

        db.insertWithOnConflict(TABLE_WATCHED_EPISODES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public boolean isEpisodeWatched(String showId, int seasonNum, int episodeNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_WATCHED + " FROM " + TABLE_WATCHED_EPISODES + 
                      " WHERE " + COLUMN_SHOW_ID + " = ? AND " + 
                      COLUMN_SEASON_NUM + " = ? AND " +
                      COLUMN_EPISODE_NUM + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{showId, String.valueOf(seasonNum), String.valueOf(episodeNum)});
        
        boolean isWatched = false;
        if (cursor.moveToFirst()) {
            isWatched = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WATCHED)) == 1;
        }
        
        cursor.close();
        db.close();
        return isWatched;
    }
    public Cursor getWatchedEpisodes(String showId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WATCHED_EPISODES,
                null,
                COLUMN_WATCHED_SEASON_ID + " = ? AND " + COLUMN_WATCHED + " = 1",
                new String[]{showId},
                null, null,
                null);
    }

}