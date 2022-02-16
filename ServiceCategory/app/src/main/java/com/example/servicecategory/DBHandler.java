package com.example.servicecategory;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "allservices";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME1 = "category";
    private static final String TABLE_NAME2 = "services";

    // below variable is for our id column.
    private static final String ID_COL = "catid";

    // below variable is for our name column
    private static final String NAME_COL1 = "catname";
    private static final String NAME_COL2 = "servicename";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
//        db.execSQL("delete from " + TABLE_NAME1);
//        db.execSQL("delete from " + TABLE_NAME2);

        String query1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 + " ("
                + ID_COL + " INTEGER, "
                + NAME_COL1 + " TEXT)";

        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " ("
                + ID_COL + " INTEGER, "
                + NAME_COL2 + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query1);
        db.execSQL(query2);
    }

    public void insertData(int id, String catname, String tableName) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

//        db.execSQL("DROP TABLE " + TABLE_NAME1);
//        db.execSQL("DROP TABLE " + TABLE_NAME2);

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        if(tableName.equals(TABLE_NAME1)) {
            values.put(ID_COL, id);
            values.put(NAME_COL1, catname);
        } else if (tableName.equals(TABLE_NAME2)) {
            values.put(ID_COL, id);
            values.put(NAME_COL2, catname);
        }

        // after adding all values we are passing
        // content values to our table.
        if(tableName.equals(TABLE_NAME1)) {
            db.insert(TABLE_NAME1, null, values);
            values.clear();
        } else if (tableName.equals(TABLE_NAME2)) {
            db.insert(TABLE_NAME2, null, values);
            values.clear();
        }
        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the courses.
    @SuppressLint("Range")
    public List<String> readTitles() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();
        // on below line we are creating a new array list.
        List<String> titlesList = new ArrayList<>();
        // on below line we are creating a cursor with query to read data from database.
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME1, null);
        while(c.moveToNext()) {
            titlesList.add(c.getString(c.getColumnIndex("catname")));
//            titlesList.add(c.getString(c.getColumnIndex("catid")));
        }
        c.close();
        return titlesList;
    }

    @SuppressLint("Range")
    public HashMap<String, List<String>> readListDetail() {
        SQLiteDatabase db = this.getReadableDatabase();

        HashMap<String, List<String>> listDetail = new HashMap<>();

        // Get a cursor for category table
        Cursor cat = db.rawQuery("SELECT * FROM " + TABLE_NAME1 + " ORDER BY catid ASC", null);
        cat.moveToFirst();
        // Get a cursos for service table
        Cursor serv = db.rawQuery("SELECT * FROM " + TABLE_NAME2 + " ORDER BY catid ASC", null);

        // List to store services
        List<String> serviceList = new ArrayList<>();
        // each id from category table
        int idcat;
        // each id from service table
        int idserv;
        // String to store category name
        String category = cat.getString(cat.getColumnIndex("catname"));
        idcat = cat.getInt(cat.getColumnIndex("catid"));

        while(serv.moveToNext()) {
            idserv = serv.getInt(serv.getColumnIndex("catid"));
            if(idcat != idserv) {
                cat.moveToNext();
                idcat = cat.getInt(cat.getColumnIndex("catid"));
                category = cat.getString(cat.getColumnIndex("catname"));
                listDetail.put(category , new ArrayList(serviceList));
                serviceList.clear();
                serviceList.add(serv.getString(serv.getColumnIndex("servicename")));
                continue;
            }
            serviceList.add(serv.getString(serv.getColumnIndex("servicename")));
        }
        serv.close();
        cat.close();
        return listDetail;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }
}