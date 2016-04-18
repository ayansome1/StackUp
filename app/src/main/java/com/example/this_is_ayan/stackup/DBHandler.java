package com.example.this_is_ayan.stackup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBHandler extends SQLiteOpenHelper
{


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Database.db";
    private static final String TABLE_NAME = "tb";
    private static final String KEY_ID = "_id";


    String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+KEY_ID+" TEXT PRIMARY KEY)";

    String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    //@Override
    public void addQuestion(String questionID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ID, questionID);
            db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    public void deleteQuestion(String questionID)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String statement="DELETE FROM "+TABLE_NAME+" WHERE "+KEY_ID+" = "+"'"+questionID+"'";
            db.execSQL(statement);



           // ContentValues values = new ContentValues();
            //values.put(KEY_ID, questionID);
            //db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }



    // @Override
    public ArrayList<String> getAllQuestions()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> questionList = new ArrayList<String>();

        try{
            String QUERY = "SELECT * FROM "+TABLE_NAME;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    String qq;
                    qq=cursor.getString(0);
                    questionList.add(qq);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return questionList;
    }

   // @Override
    public int getQuestionCount()
    {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            String QUERY = "SELECT * FROM "+TABLE_NAME;

            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }
}