package com.m.freemovie.Utils.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.m.freemovie.mvp.ClassBean.DetailBean;

import java.util.ArrayList;
import java.util.List;

public class BookmarkDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookmarks.db";
    private static final int DATABASE_VERSION = 1;

    // Bookmark table definition
    public static class BookmarkEntry {
        public static final String TABLE_NAME = "bookmarks";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IS_TV = "is_tv";
    }

    // Table creation SQL
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookmarkEntry.TABLE_NAME + " (" +
                    BookmarkEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookmarkEntry.COLUMN_VIDEO_ID + " TEXT UNIQUE NOT NULL," +
                    BookmarkEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL," +
                    BookmarkEntry.COLUMN_IMAGE_URL + " TEXT," +
                    BookmarkEntry.COLUMN_TITLE + " TEXT," +
                    BookmarkEntry.COLUMN_IS_TV + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookmarkEntry.TABLE_NAME;

    public BookmarkDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Add or remove bookmark
     */
    public boolean toggleBookmark(DetailBean detailBean, boolean isTv) {
        if (isBookmarked(detailBean.getVideoId())) {
            return removeBookmark(detailBean.getVideoId());
        } else {
            return addBookmark(detailBean, isTv);
        }
    }

    /**
     * Add a new bookmark
     */
    public boolean addBookmark(DetailBean detailBean, boolean isTv) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(BookmarkEntry.COLUMN_VIDEO_ID, detailBean.getVideoId());
        values.put(BookmarkEntry.COLUMN_TIMESTAMP, detailBean.getTimeStamp());
        values.put(BookmarkEntry.COLUMN_IMAGE_URL, detailBean.getTempImage());
        values.put(BookmarkEntry.COLUMN_TITLE, detailBean.getMovieName());
        values.put(BookmarkEntry.COLUMN_IS_TV, isTv ? 1 : 0);

        try {
            long result = db.insertWithOnConflict(
                BookmarkEntry.TABLE_NAME, 
                null, 
                values, 
                SQLiteDatabase.CONFLICT_REPLACE
            );
            return result != -1;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Remove a bookmark
     */
    public boolean removeBookmark(String videoId) {
        SQLiteDatabase db = getWritableDatabase();
        
        try {
            int result = db.delete(
                BookmarkEntry.TABLE_NAME,
                BookmarkEntry.COLUMN_VIDEO_ID + " = ?",
                new String[]{videoId}
            );
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Check if a video is bookmarked
     */
    public boolean isBookmarked(String videoId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String[] projection = {BookmarkEntry.COLUMN_ID};
            String selection = BookmarkEntry.COLUMN_VIDEO_ID + " = ?";
            String[] selectionArgs = {videoId};
            
            cursor = db.query(
                BookmarkEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            );
            
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * Get all bookmarks
     * optional
     */
    public List<DetailBean> getAllBookmarks() {
        List<DetailBean> bookmarks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {
                BookmarkEntry.COLUMN_VIDEO_ID,
                BookmarkEntry.COLUMN_TIMESTAMP,
                BookmarkEntry.COLUMN_IMAGE_URL,
                BookmarkEntry.COLUMN_TITLE,
                BookmarkEntry.COLUMN_IS_TV
            };

            String sortOrder = BookmarkEntry.COLUMN_TIMESTAMP + " DESC";

            cursor = db.query(
                BookmarkEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
            );

            while (cursor != null && cursor.moveToNext()) {
                DetailBean detail = new DetailBean();
                detail.setVideoId(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_VIDEO_ID)));
                detail.setTimeStamp(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_TIMESTAMP)));
                detail.setTempImage(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_IMAGE_URL)));
                detail.setMovieName(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_TITLE)));

                bookmarks.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return bookmarks;
    }

    /**
     * Get bookmarks by type (TV or Movie)
     */
    public List<DetailBean> getBookmarksByType(boolean isTv) {
        List<DetailBean> bookmarks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String[] projection = {
                BookmarkEntry.COLUMN_VIDEO_ID,
                BookmarkEntry.COLUMN_TIMESTAMP,
                BookmarkEntry.COLUMN_IMAGE_URL,
                BookmarkEntry.COLUMN_TITLE
            };
            
            String selection = BookmarkEntry.COLUMN_IS_TV + " = ?";
            String[] selectionArgs = {isTv ? "1" : "0"};
            String sortOrder = BookmarkEntry.COLUMN_TIMESTAMP + " DESC";
            
            cursor = db.query(
                BookmarkEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            );
            
            while (cursor != null && cursor.moveToNext()) {
                DetailBean detail = new DetailBean();
                detail.setVideoId(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_VIDEO_ID)));
                detail.setTimeStamp(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_TIMESTAMP)));
                detail.setTempImage(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_IMAGE_URL)));
                detail.setMovieName(cursor.getString(cursor.getColumnIndexOrThrow(BookmarkEntry.COLUMN_TITLE)));
                
                bookmarks.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        
        return bookmarks;
    }
}