package d.swan.mobileseller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by daniel on 6/9/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    ContentValues contentValues = new ContentValues();

    public DataBaseHelper(Context context) {
        super(context, "dBase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Organization (" +
                "org_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Address (" +
                "address_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL, " +
                "org_id INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    public void addField(String table, String name) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        contentValues.put("name", name);

        sqLiteDatabase.insert(table, null, contentValues);

        sqLiteDatabase.close();
    }

    public void addField(String table, String name, int org_id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        contentValues.put("name", name);
        contentValues.put("org_id", org_id);

        sqLiteDatabase.insert(table, null, contentValues);
        sqLiteDatabase.close();
    }

    public void deleteField(String table, int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(table, "org_id = " + id, null);
        sqLiteDatabase.close();

    }

    public ArrayList<String> getAllValues(String table) {

        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(table, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");

            do {
                result.add(cursor.getString(nameColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public ArrayList<String> getAllValues(String table, int org_id) {
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(table, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("org_id");
            int nameColIndex = cursor.getColumnIndex("name");

            do {
                if (cursor.getInt(idColIndex) == org_id)
                    result.add(cursor.getString(nameColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public boolean identityVerification(String table, String name) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(table, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");

            do {
                if (name.equals(cursor.getString(nameColIndex)))
                    return true;
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return false;
    }
}
