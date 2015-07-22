package smarthouse.smartronic2;

/**
 * Created by burak on 19/03/15.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Smartronic";


    String USER = "user";
    String LAMP = "lamp";
    String HEATER = "heater";
    String HOME = "home";
    String PLUG = "plug";
    String ROOM = "room";
    String SECURITY = "security";

    String ID = "id";
    String ROOM_NAME = "room_name";
    String STATUS = "status";
    String OWNER_ID = "owner_id";
    String ROOM_ID = "room_id";
    String HOME_ID = "home_id";
    String PLUG_ID = "plug_id";
    String HEATER_ID = "heater_id";
    String CURTAIN_ID = "curtain_id";
    String LAMP_ID = "lamp_id";
    String TYPE = "type";
    String USERNAME = "username";
    String PASSWORD = "password";
    String ADDRESS = "address";
    String TEL_NO = "tel_no";
    String EMAIL = "email";
    String NAME = "name";
    String SURNAME = "surname";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER = "CREATE TABLE" + " " + USER + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USERNAME +
                " TEXT," + PASSWORD + " TEXT," + HOME_ID + " TEXT)";
        db.execSQL(CREATE_USER);

        String CREATE_SECURITY = "CREATE TABLE" + " " + SECURITY + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ROOM_ID +
                " TEXT," + HOME_ID + " TEXT," + STATUS + " TEXT," + TYPE + " TEXT)";
        db.execSQL(CREATE_SECURITY);

        String CREATE_ROOM = "CREATE TABLE" + " " + ROOM + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + "room_xml_id"
                + " TEXT," + NAME + " TEXT," + PLUG_ID + " TEXT," + HEATER_ID + " TEXT," + CURTAIN_ID + " TEXT," +
                LAMP_ID + " TEXT)";
        db.execSQL(CREATE_ROOM);

        String CREATE_PLUG = "CREATE TABLE" + " " + PLUG + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + STATUS + " TEXT," + HOME_ID + " TEXT," + "plug_xml_id" + " TEXT," + ROOM_ID + " TEXT)";
        db.execSQL(CREATE_PLUG);

        String CREATE_HOME = "CREATE TABLE" + " " + HOME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + OWNER_ID +
                " TEXT," + ROOM + " TEXT)";
        db.execSQL(CREATE_HOME);

        String CREATE_HEATER = "CREATE TABLE" + " " + HEATER + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ROOM_ID + " TEXT," + "heater_xml_id" + " TEXT," + STATUS + " TEXT," + HOME_ID + " TEXT)";
        db.execSQL(CREATE_HEATER);

        String CREATE_LAMP = "CREATE TABLE" + " " + LAMP + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ROOM_ID + " TEXT," + STATUS + " TEXT," + "lamp_xml_id" + " TEXT," + HOME_ID + " TEXT)";
        db.execSQL(CREATE_LAMP);

    }

    public void deleteRoom(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ROOM, "room_xml_id" + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public String getRoomText(String id) {
        String query = "SELECT" + " " + NAME + " FROM " + ROOM + " where room_xml_id=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String response = cursor.toString();
        db.close();
        cursor.close();
        return response;
    }

    public void updateRoomText(String id, String value) {
        String input = getRoomText(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, value);

        db.update(ROOM, values, input, new String[]{String.valueOf(value)});
    }

    public void setLayoutId(String generatedButtonId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("room_xml_id", generatedButtonId);

        db.update(ROOM, values, name, new String[]{String.valueOf(generatedButtonId)});
    }

    public void addRoom(String id, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("room_xml_id", id);
        values.put(NAME, value);

        db.insert(ROOM, null, values);
        db.close();
    }

    public int getRowCount(String table) {
        String countQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    public int getRoomDeviceCount(int type, int id) {
        String query = "";
        String ID = getIdWithXMLId(id);
        int response = 0;
        if (type == 0) {
            query = "SELECT " + PLUG_ID +  "FROM " + ROOM + " WHERE " + this.ID + "=" + ID;
        }
        if (type == 1) {
            query = "SELECT " + HEATER_ID + "FROM " + ROOM + " WHERE " + this.ID + "=" + ID;
        }
        if (type == 2) {
            query = "SELECT " + LAMP_ID +  "FROM " + ROOM + " WHERE " + this.ID + "=" + ID;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        db.close();
        cursor.close();
        return cursor.getCount();
    }

    public ArrayList<String> getDeviceName(int id, int type) {
        String query = "";
        String ID = getIdWithXMLId(id);
        if (type == 0) {
            query = "SELECT name FROM " + PLUG + " WHERE " + ROOM_ID + "=" + ID;
        } else if (type == 1) {
            query = "SELECT name FROM " + HEATER + " WHERE " + ROOM_ID + "=" + ID;
        } else if (type == 2) {
            query = "SELECT name FROM " + LAMP + " WHERE " + ROOM_ID + "=" + ID;
        } else {
            // do nothing
            query = "";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String response = cursor.getString(cursor.getColumnIndex("name"));
            arrayList.add(response);
        }
        return arrayList;
    }

    public ArrayList<String> getDeviceStatus(int id, int type) {
        String query = "";
        String ID = getIdWithXMLId(id);
        if (type == 0) {
            query = "SELECT status FROM " + PLUG + " WHERE " + ROOM_ID + "=" + ID;
        } else if (type == 1) {
            query = "SELECT status FROM " + HEATER + " WHERE " + ROOM_ID + "=" + ID;
        } else if (type == 2) {
            query = "SELECT status FROM " + LAMP + " WHERE " + ROOM_ID + "=" + ID;
        } else {
            // do nothing
            query = "";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String response = cursor.getString(cursor.getColumnIndex("status"));
            arrayList.add(response);
        }
        return arrayList;
    }

    public String getIdWithXMLId(int id) {
        String query = "SELECT" + " room_xml_id FROM " + ROOM + " where " + ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String response = cursor.toString();
        db.close();
        cursor.close();
        return response;
    }

    public void updateStatusOnOff(int id, String status, int type) {
        String table = "";
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        if (type == 0) {
            table = PLUG;
        }
        if (type == 1) {
            table = HEATER;
        }
        if (type == 2) {
            table = LAMP;
        }
        db.update(table, contentValues, "id = " + id, null);
    }

    public String getOnOffStatus(int id, int type) {
        String query = "";
        String response = "";
        if (type == 0) {
            query = "SELECT status FROM " + PLUG + " WHERE " + ID + " = '" + id + "'";
        }
        if (type == 1) {
            query = "SELECT status FROM " + HEATER + " WHERE " + ID + " = '" + id + "'";
        }
        if (type == 2) {
            query = "SELECT status FROM " + LAMP + " WHERE " + ID + " = '" + id + "'";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        response = cursor.toString();
        db.close();
        cursor.close();

        return response;
    }

    public boolean loginCheck(String password, String username) {
        String query = "SELECT * FROM " + USER + " WHERE " + PASSWORD + " = '" + password + "'" +
                " AND " + USERNAME + " = '" + username + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        int rowCount;
        if (cursor == null) {
            rowCount = 0;
        } else {
            rowCount = cursor.getCount();
        }
        db.close();
        cursor.close();
        if (!(rowCount == 1)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXIST" + USER);
        onCreate(db);

    }
}
