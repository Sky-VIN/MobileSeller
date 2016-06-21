package d.swan.mobileseller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 6/9/16.
 */
public final class DataBaseHelper extends SQLiteOpenHelper {

    ContentValues contentValues = new ContentValues();

    public DataBaseHelper(Context context) {
        super(context, "dBase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Organization (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "linked_id INTEGER NOT NULL);"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS Address (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "linked_id INTEGER NOT NULL);"
        );


        db.execSQL("CREATE TABLE IF NOT EXISTS MailSettings (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "user TEXT NOT NULL," +
                "sender TEXT NOT NULL," +
                "pass TEXT NOT NULL," +
                "smtp TEXT NOT NULL," +
                "port TEXT NOT NULL," +
                "receiver TEXT NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }


    public String[] loadMailSettings() {
        String[] result = new String[]{"", "", "", "", "", ""};

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MailSettings", null);


        if (cursor.moveToFirst()) {
            int userColIndex = cursor.getColumnIndex("user");
            int senderColIndex = cursor.getColumnIndex("sender");
            int passColIndex = cursor.getColumnIndex("pass");
            int smtpColIndex = cursor.getColumnIndex("smtp");
            int portColIndex = cursor.getColumnIndex("port");
            int receiverColIndex = cursor.getColumnIndex("receiver");
            do {
                Log.d("Mseller", String.valueOf(userColIndex + " " + senderColIndex + " " + passColIndex + " " + smtpColIndex + " " + portColIndex + " " + receiverColIndex));
                result[0] = cursor.getString(userColIndex);
                result[1] = cursor.getString(senderColIndex);
                result[2] = cursor.getString(passColIndex);
                result[3] = cursor.getString(smtpColIndex);
                result[4] = cursor.getString(portColIndex);
                result[5] = cursor.getString(receiverColIndex);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public void saveMailSettings(String[] settings) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put("user", settings[0]);
        contentValues.put("sender", settings[1]);
        contentValues.put("pass", settings[2]);
        contentValues.put("smtp", settings[3]);
        contentValues.put("port", settings[4]);
        contentValues.put("receiver", settings[5]);

        sqLiteDatabase.update("MailSettings", contentValues, "id = ?", new String[]{"1"});
        sqLiteDatabase.close();
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
    public List<String> getAllValues(String table) {

        ArrayList<String> result = new ArrayList<String>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");

            int i = 0;
            do {
                result.add(cursor.getString(nameColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    // Сбор и возврат всех значений имен из заданой таблицы по связующему ключу
    public List<String> getAllValues(String table, int linked_id) {

        List<String> result = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);

        if (cursor.moveToFirst()) {
            int linkedIdColIndex = cursor.getColumnIndex("linked_id");
            int nameColIndex = cursor.getColumnIndex("name");

            int i = 0;
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
                        if (newLinkedId == i) {
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

/*
    // сбор списка с розничными ценами
    public ArrayList<Point> getRetailPrice() {
        ArrayList<Point> priceArray = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Price", null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");
            int priceColIndex = cursor.getColumnIndex("retail_price");

            do {
                priceArray.add(new Point(cursor.getString(nameColIndex), cursor.getFloat(priceColIndex), 0, 0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return priceArray;
    }

    // сбор списка с оптовыми ценами
    public ArrayList<Point> getWholesalePrice() {
        ArrayList<Point> priceArray = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Price", null);

        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex("name");
            int priceColIndex = cursor.getColumnIndex("wholesale_price");

            do {
                priceArray.add(new Point(cursor.getString(nameColIndex), cursor.getFloat(priceColIndex), 0, 0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return priceArray;
    }
*/

    // Вывод в лог всех полей

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

    public void deletePrice() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM Price");
        sqLiteDatabase.close();
    }

    public void setPrice(String name, float wholescapePrice, float retailPrice) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        contentValues.put("name", name);
        contentValues.put("wholesale_price", wholescapePrice);
        contentValues.put("retail_price", retailPrice);

        sqLiteDatabase.insert("Price", null, contentValues);
        sqLiteDatabase.close();
    }


}


