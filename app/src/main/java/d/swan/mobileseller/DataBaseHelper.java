package d.swan.mobileseller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by daniel on 6/9/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    ContentValues contentValues = new ContentValues();

    public DataBaseHelper(Context context) {
        super(context, "dBase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Organization (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "linked_id INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Address (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "linked_id INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }


    // Добавление поля и связующего ключа (для организации)
    public void addField(String table, String name, int linked_id) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        contentValues.put("name", name);
        contentValues.put("linked_id", linked_id);

        sqLiteDatabase.insert(table, null, contentValues);
        sqLiteDatabase.close();
    }

    // Удаление поля по связующей ссылке (для организации)
    public void deleteField(String table, int linked_id) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(table, "linked_id = " + linked_id, null);
        sqLiteDatabase.close();
    }

    // Удаление поля по имени (для адреса)
    public void deleteField(String table, String name) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(table, "name = \"" + name + "\"", null);
        sqLiteDatabase.close();
    }

    // поиск связующего ключа по имени
    public int getLinkedIdByName(String table, String name) {

        int result = 0;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");
            int linkedIdColIndex = cursor.getColumnIndex("linked_id");

            do {
                if (name.equals(cursor.getString(nameColIndex)))
                    return cursor.getInt(linkedIdColIndex);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return result;

    }

    // Сбор и возврат всех значений имен из заданой таблицы
    public ArrayList<String> getAllValues(String table) {

        ArrayList<String> result = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

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

    // Сбор и возврат всех значений имен из заданой таблицы по связующему ключу
    public ArrayList<String> getAllValues(String table, int linked_id) {

        ArrayList<String> result = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

        if (cursor.moveToFirst()) {
            int linkedIdColIndex = cursor.getColumnIndex("linked_id");
            int nameColIndex = cursor.getColumnIndex("name");

            do {
                if (cursor.getInt(linkedIdColIndex) == linked_id)
                    result.add(cursor.getString(nameColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    // проверка на идентичность имени
    public boolean identityVerification(String table, String name) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

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

    // поиск свободного связующего ключа
    public int findFreeLinkedId() {

        int newLinkedId = 0;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Organization;", null);

        if (cursor.getCount() > 0) {

            int[] linkedIdArray = new int[cursor.getCount()];
            int linkedIdColIndex = cursor.getColumnIndex("linked_id");

            // заполнение массива всеми имеющимися связными ключами
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    linkedIdArray[i] = cursor.getInt(linkedIdColIndex);
                    cursor.moveToNext();
                }

                // нахождение свободного связующего ключа
                boolean b;
                do {
                    b = false;
                    newLinkedId++;

                    for (int i : linkedIdArray)
                        if (newLinkedId == linkedIdArray[i]) {
                            b = true;
                            break;
                        }
                } while (b);
            }

        } else newLinkedId = 1;

        cursor.close();
        sqLiteDatabase.close();

        return newLinkedId;
    }

    // Вывод в лог всез полей
    public void getAllLog(String table) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int nameColInex = cursor.getColumnIndex("name");
            int linkedIdColIndex = cursor.getColumnIndex("linked_id");

            Log.d("Mseller", "- - - - - - - - - - - - - - - - - - - - - - - - -");
            do {
                Log.d("Mseller", "id = " + cursor.getInt(idColIndex) + "\tname = " + cursor.getString(nameColInex) + "\tlinked_id = " + cursor.getInt(linkedIdColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
    }
}
