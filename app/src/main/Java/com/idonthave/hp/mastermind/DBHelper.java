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

    public static final String HIGHSCORES_COLUMN_ID = "id";
    public static final String HIGHSCORES_COLUMN_HIGHSCORE = "highscore";

    public static final String SAVEDGAME_COLUMN_ID = "id";
    public static final String SAVEDGAME_COLUMN_NUMBEROFTRIES = "numberoftries";
    public static final String SAVEDGAME_COLUMN_NUMBEROFCOLORS = "numberofcolors";
    public static final String SAVEDGAME_COLUMN_NUMBEROFSLOTS = "numberofslots";
    public static final String SAVEDGAME_COLUMN_COLORS = "colors";
    public static final String SAVEDGAME_COLUMN_ALLOWDUPLICATE = "allowduplicates";
    public static final String SAVEDGAME_COLUMN_ALLOWEMPTY = "allowempty";

    public static final String SAVEDMOVE_COLUMN_ID = "id";
    public static final String SAVEDMOVE_COLUMN_ROW = "row";
    public static final String SAVEDMOVE_COLUMN_COLUMN = "column";
    public static final String SAVEDMOVE_COLUMN_RESOURCE = "resource";
    private HashMap hp;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+HIGHSCORES_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertHighscore (String highscore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HIGHSCORES_COLUMN_HIGHSCORE, highscore);
        db.insert(HIGHSCORES_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ HIGHSCORES_TABLE_NAME +" where "+ HIGHSCORES_COLUMN_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, HIGHSCORES_TABLE_NAME);
        return numRows;
    }

    public boolean updateHighscore (Integer id, String highscore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("highscore", highscore);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteHighscore (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(HIGHSCORES_TABLE_NAME,
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
        return array_list;
    }
}