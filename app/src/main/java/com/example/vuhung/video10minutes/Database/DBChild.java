package com.example.vuhung.video10minutes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Model.Child;

import java.util.ArrayList;

public class DBChild extends SQLiteOpenHelper  {
    public static final String DATABASE_NAME ="alarm1";
    private static final String TABLE_NAME ="child1";
    private static final String ID ="id";
    private static final String NAME ="name";
    private static final String PHOTO ="photo";

    private Context context;

    public DBChild(Context context) {
        super(context, DATABASE_NAME,null, 1);
        Log.d("DBChild", "DBChild: ");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID +" INTEGER, "+
                NAME + " TEXT, "+
                PHOTO +" TEXT)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create successfylly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllChild(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    //Add new a Child
    public void addChild(Child child){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID,child.getId());
        values.put(NAME, child.getName());
        values.put(PHOTO,child.getPhoto());
        //Neu de null thi khi value bang null thi loi
        db.insert(TABLE_NAME,null,values);
        Toast.makeText(context,"Add completed",Toast.LENGTH_SHORT).show();
        db.close();
    }

    /*
    Select a Child by ID
     */

    public Child getChildByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Child child = new Child();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, PHOTO }, NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                child.setId(cursor.getInt(0));
                child.setName(cursor.getString(1));
                child.setPhoto(cursor.getString(2));
            }
        }
        cursor.close();
        db.close();
        return child;
    }
    public Child getChildById(int iD){
        SQLiteDatabase db = this.getReadableDatabase();
        Child child = new Child();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, PHOTO }, ID + "=?",
                new String[] {String.valueOf(iD)}, null, null, null, null);
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                child.setId(cursor.getInt(0));
                child.setName(cursor.getString(1));
                child.setPhoto(cursor.getString(2));
            }
        }
        cursor.close();
        db.close();
        return child;
    }
    /*
    Update name of child
     */

    public void update(int Id, Child child){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME,child.getName());
        values.put(PHOTO,child.getPhoto());
        db.update(TABLE_NAME,values,ID +"="+Id,null);

    }

    /*
     Getting All Child
      */

    public ArrayList<Child> getAllChild() {
        ArrayList<Child> listChild = new ArrayList<Child>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Child child = new Child();
                child.setId(cursor.getInt(0));
                child.setName(cursor.getString(1));
                child.setPhoto(cursor.getString(2));

                listChild.add(child);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listChild;
    }
    /*
    Delete a Child by ID
     */
    public void deleteChild(Child child) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[] { String.valueOf(child.getId()) });
        db.close();
    }
    /*
    Get Count Child in Table Child
     */
    public int getChildrenCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }
}
