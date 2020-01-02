package engidea.imgurupload.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import engidea.imgurupload.utils.aLog;

public class ImgurDB {
    private final static String TAG = "ei_i";

    private static final String DB_NAME = "IMGUR_DB";
    private static final int version = 1;

    public static final class TabImgUrl {
        static final String TAB_NAME = "IMG_URL";
        static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_URL = "url";

        private static final String QUERY_CREATE_TAB = "CREATE TABLE " + TAB_NAME
                + " ( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COL_NAME + " TEXT, " + COL_URL + " TEXT);";

        private static final String QUERY_DROP_TAB_IF_EXISTS = "DROP TABLE IF EXISTS " + TAB_NAME + ";";
    }

    private final Context context;
    private DBOpenHelper dbOpenHelper;

    public ImgurDB(Context context) {
        this.context = context;
        dbOpenHelper = new DBOpenHelper();
    }

    @SuppressWarnings("unused")
    public void clearTables() {
        try {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            db.execSQL(TabImgUrl.QUERY_DROP_TAB_IF_EXISTS);
            db.execSQL(TabImgUrl.QUERY_CREATE_TAB);
        } catch (SQLiteException sqle) {
            aLog.w(TAG, sqle.getMessage());
        }
    }

    // Returns long - the row ID of the newly inserted row, or -1 if an error occurred
    @SuppressWarnings("UnusedReturnValue")
    public long insertUrl(String name, String url) {
        try {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            ContentValues cv = new ContentValues(2);
            cv.put(TabImgUrl.COL_NAME, name);
            cv.put(TabImgUrl.COL_URL, url);
            return db.insert(TabImgUrl.TAB_NAME, null, cv);
        } catch (SQLiteException sqle) {
            aLog.w(TAG, sqle.getMessage());
            return -1;
        }
    }

    @Nullable
    public Cursor queryUrls() {
        try {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            return db.query(TabImgUrl.TAB_NAME, null, null, null,
                    null, null, TabImgUrl.COL_ID + " DESC");
        } catch (SQLiteException sqle) {
            aLog.w(TAG, sqle.getMessage());
            return null;
        }
    }

    private class DBOpenHelper extends SQLiteOpenHelper {
        DBOpenHelper() {
            super(context, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(TabImgUrl.QUERY_CREATE_TAB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int v1, int v2) {
            onCreate(sqLiteDatabase);
        }
    }
}
