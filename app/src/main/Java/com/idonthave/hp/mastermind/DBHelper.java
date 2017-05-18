package com.idonthave.hp.mastermind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Mastermind.db";
    public static final String HIGHSCORES_TABLE_NAME = "highscores";
    public static final String SAVEDGAME_TABLE_NAME = "savedconfig";
    public static final String SAVEDMOVE_TABLE_NAME = "savedmoves";
    public static final String HIDDEN_TABLE_NAME = "hidden";

    public static final String HIGHSCORES_COLUMN_ID = "id";
    public static final String HIGHSCORES_COLUMN_HIGHSCORE = "highscore";

    public static final String SAVEDGAME_COLUMN_ID = "id";
    public static final String SAVEDGAME_COLUMN_NUMBEROFTRIES = "numberoftries";
    public static final String SAVEDGAME_COLUMN_NUMBEROFCOLORS = "numberofcolors";
    public static final String SAVEDGAME_COLUMN_NUMBEROFSLOTS = "numberofslots";
    public static final String SAVEDGAME_COLUMN_COLORS = "colors";
    public static final String SAVEDGAME_COLUMN_ALLOWDUPLICATE = "allowduplicates";
    public static final String SAVEDGAME_COLUMN_ALLOWEMPTY = "allowempty";
    public static final String SAVEDGAME_COLUMN_ALLOWHIGHSCORE = "allowhighscore";
    public static final String SAVEDGAME_COLUMN_HIGHSCORE = "highscore";
    public static final String SAVEDGAME_COLUMN_OVAL = "oval";

    public static final String SAVEDMOVE_COLUMN_ID = "id";
    public static final String SAVEDMOVE_COLUMN_ROW = "row";
    public static final String SAVEDMOVE_COLUMN_COLUMN = "column";
    public static final String SAVEDMOVE_COLUMN_RESOURCE = "resource";

    public static final String HIDDEN_COLUMN_ID = "id";
    public static final String HIDDEN_COLUMN_NAME = "name";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + HIGHSCORES_TABLE_NAME +
                        "("+ HIGHSCORES_COLUMN_ID +" integer primary key, "+ HIGHSCORES_COLUMN_HIGHSCORE +" text)"
        );
        db.execSQL(
                "create table " + SAVEDGAME_TABLE_NAME +
                        "("+ SAVEDGAME_COLUMN_ID +" integer primary key, "+
                        SAVEDGAME_COLUMN_NUMBEROFTRIES +" text, "+
                        SAVEDGAME_COLUMN_NUMBEROFCOLORS +" text, "+
                        SAVEDGAME_COLUMN_NUMBEROFSLOTS +" text, "+
                        SAVEDGAME_COLUMN_COLORS +" text, "+
                        SAVEDGAME_COLUMN_ALLOWDUPLICATE +" text, "+
                        SAVEDGAME_COLUMN_ALLOWHIGHSCORE +" text, "+
                        SAVEDGAME_COLUMN_HIGHSCORE +" text, "+
                        SAVEDGAME_COLUMN_OVAL +" text, "+
                        SAVEDGAME_COLUMN_ALLOWEMPTY +" text)"
        );
        db.execSQL(
                "create table " + SAVEDMOVE_TABLE_NAME +
                        "("+ SAVEDMOVE_COLUMN_ID +" integer primary key, "+
                        SAVEDMOVE_COLUMN_RESOURCE +" text, "+
                        SAVEDMOVE_COLUMN_ROW +" text, "+
                        SAVEDMOVE_COLUMN_COLUMN +" text)"
        );
        db.execSQL(
                "create table " + HIDDEN_TABLE_NAME +
                        "("+ HIDDEN_COLUMN_ID +" integer primary key, "+
                        HIDDEN_COLUMN_NAME +" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+HIGHSCORES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SAVEDMOVE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SAVEDGAME_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+HIDDEN_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertHighscore (String highscore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HIGHSCORES_COLUMN_HIGHSCORE, highscore);
        db.insert(HIGHSCORES_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertSavedgame (String numberoftries, String numberofcolors, String numberofslots, String colors, String allowduplicates, String allowempty, String allowhighscore, String highscore, String ovalPins) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SAVEDGAME_COLUMN_NUMBEROFTRIES, numberoftries);
        contentValues.put(SAVEDGAME_COLUMN_NUMBEROFSLOTS, numberofslots);
        contentValues.put(SAVEDGAME_COLUMN_NUMBEROFCOLORS, numberofcolors);
        contentValues.put(SAVEDGAME_COLUMN_COLORS, colors);
        contentValues.put(SAVEDGAME_COLUMN_ALLOWDUPLICATE, allowduplicates);
        contentValues.put(SAVEDGAME_COLUMN_ALLOWEMPTY, allowempty);
        contentValues.put(SAVEDGAME_COLUMN_ALLOWHIGHSCORE, allowhighscore);
        contentValues.put(SAVEDGAME_COLUMN_HIGHSCORE, highscore);
        contentValues.put(SAVEDGAME_COLUMN_OVAL, ovalPins);
        db.insert(SAVEDGAME_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertSavedmove (String rescource, String row, String column) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SAVEDMOVE_COLUMN_RESOURCE, rescource);
        contentValues.put(SAVEDMOVE_COLUMN_ROW, row);
        contentValues.put(SAVEDMOVE_COLUMN_COLUMN, column);
        db.insert(SAVEDMOVE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertHidden (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HIDDEN_COLUMN_NAME, name);
        db.insert(HIDDEN_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ HIGHSCORES_TABLE_NAME +" where "+ HIGHSCORES_COLUMN_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRowsHighscore(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, HIGHSCORES_TABLE_NAME);
        return numRows;
    }

    public int numberOfRowsSavedgame(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SAVEDGAME_TABLE_NAME);
        return numRows;
    }

    public boolean updateHighscore (Integer id, String highscore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("highscore", highscore);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteSavedgame (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SAVEDGAME_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public String[] getTopHighscores() {
        String[] array_list = new String[10];

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select "+ HIGHSCORES_COLUMN_HIGHSCORE +" from "+ HIGHSCORES_TABLE_NAME +" order by "+ HIGHSCORES_COLUMN_HIGHSCORE +" desc LIMIT 10;", null );
        res.moveToFirst();

        int i = 0;
        while(res.isAfterLast() == false){
            array_list[i]  = res.getString(res.getColumnIndex(HIGHSCORES_COLUMN_HIGHSCORE));
            res.moveToNext();
            i++;
        }
        while(i<10){
            array_list[i] = "000000000000";
            i++;
        }
        return array_list;
    }

    public String [] getSavedgame(){
        String[] array_list = new String[9];

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select "+ SAVEDGAME_COLUMN_NUMBEROFTRIES +" , " + SAVEDGAME_COLUMN_NUMBEROFSLOTS +" , " + SAVEDGAME_COLUMN_NUMBEROFCOLORS + " , " + SAVEDGAME_COLUMN_COLORS +" , " + SAVEDGAME_COLUMN_ALLOWDUPLICATE + " , " + SAVEDGAME_COLUMN_ALLOWEMPTY + " , " + SAVEDGAME_COLUMN_ALLOWHIGHSCORE + " , " + SAVEDGAME_COLUMN_HIGHSCORE + " , " + SAVEDGAME_COLUMN_OVAL +" from "+ SAVEDGAME_TABLE_NAME +" desc LIMIT 1;", null );
        res.moveToFirst();

        array_list[0] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_NUMBEROFTRIES));
        array_list[1] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_NUMBEROFSLOTS));
        array_list[2] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_NUMBEROFCOLORS));
        array_list[3] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_COLORS));
        array_list[4] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_ALLOWDUPLICATE));
        array_list[5] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_ALLOWEMPTY));
        array_list[6] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_ALLOWHIGHSCORE));
        array_list[7] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_HIGHSCORE));
        array_list[8] = res.getString(res.getColumnIndex(SAVEDGAME_COLUMN_OVAL));
        return array_list;
    }

    public ArrayList<String> getSavedmove() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SAVEDMOVE_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(SAVEDMOVE_COLUMN_RESOURCE)));
            array_list.add(res.getString(res.getColumnIndex(SAVEDMOVE_COLUMN_ROW)));
            array_list.add(res.getString(res.getColumnIndex(SAVEDMOVE_COLUMN_COLUMN)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getHidden() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + HIDDEN_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(HIDDEN_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean deleteAllSavedgame(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+SAVEDGAME_TABLE_NAME);
        return true;
    }

    public boolean deleteAllSavedmove(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+SAVEDMOVE_TABLE_NAME);
        return true;
    }

    public boolean deleteAllHidden(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+HIDDEN_TABLE_NAME);
        return true;
    }
}