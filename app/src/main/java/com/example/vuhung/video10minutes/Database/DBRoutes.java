package com.example.vuhung.video10minutes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Model.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class DBRoutes extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="alarm";
    private static final String TABLE_NAME ="routes";
    private static final String ID ="id";
    private static final String NAME ="name";
    private static final String CHILDREN ="children";
    private static final String ICON ="icon";
    private static final String TIME = "time";
    private static final String TIME_CURRENT ="time_current";

    private Context context;

    public DBRoutes(Context context) {
        super(context, DATABASE_NAME,null, 1);
        Log.d("DBRoutes", "DBRoutes: ");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID +" INTEGER, "+
                NAME + " TEXT, "
                +CHILDREN +" BLOB, "
                +ICON +" INTEGER, "
                +TIME_CURRENT +" INTEGER, "
                +TIME +" INTEGER)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create successfylly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllRoutes(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    //Add new a Route
    public void addRoutes(Route route){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID,route.getId());
        values.put(NAME, route.getName());
        ArrayList<Child> children  = route.getListChildren();
        Gson gson = new Gson();
        values.put(CHILDREN, gson.toJson(children).getBytes());
        values.put(ICON,route.getIcon());
        values.put(TIME_CURRENT,route.getTimeCurrent());
        values.put(TIME,route.getTime());
        //Neu de null thi khi value bang null thi loi
        db.insert(TABLE_NAME,null,values);
        Toast.makeText(context,"Add completed",Toast.LENGTH_SHORT).show();
        db.close();
    }


    public Route getRouteByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Route route = new Route();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, CHILDREN, ICON, TIME_CURRENT,TIME }, NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                route.setId(cursor.getInt(0));
                route.setName(cursor.getString(1));
                byte[] blob = cursor.getBlob(2);
                String json = new String(blob);
                Gson gson = new Gson();
                ArrayList<Child> children = gson.fromJson(json, new TypeToken<ArrayList<Child>>()
                {}.getType());
                route.setListChildren(children);
                route.setIcon(cursor.getInt(3));
                route.setTimeCurrent(cursor.getLong(4));
                route.setTime(cursor.getLong(5));
            }
        }


        cursor.close();
        db.close();
        return route;
    }
    public Route getRouteById(int iD){
        SQLiteDatabase db = this.getReadableDatabase();
        Route route = new Route();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, CHILDREN, ICON, TIME_CURRENT,TIME }, ID + "=?",
                new String[] {String.valueOf(iD)}, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                route.setId(cursor.getInt(0));
                route.setName(cursor.getString(1));
                byte[] blob = cursor.getBlob(2);
                String json = new String(blob);
                Gson gson = new Gson();
                ArrayList<Child> children = gson.fromJson(json, new TypeToken<ArrayList<Child>>()
                {}.getType());
                route.setListChildren(children);
                route.setIcon(cursor.getInt(3));
                route.setTimeCurrent(cursor.getLong(4));
                route.setTime(cursor.getLong(5));
            }
        }


        cursor.close();
        db.close();
        return route;
    }

    /*
    Update name of child
     */

    public void update(int Id, Route route){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("abcd", "update ID "+Id);
        values.put(NAME, route.getName());
        ArrayList<Child> children  = route.getListChildren();
        Gson gson = new Gson();
        values.put(CHILDREN, gson.toJson(children).getBytes());
        values.put(ICON,route.getIcon());
        values.put(TIME_CURRENT,route.getTimeCurrent());
        values.put(TIME,route.getTime());
        db.update(TABLE_NAME,values,ID +"="+Id,null);


    }

    /*
     Getting All Child
      */

    public ArrayList<Route> getAllRoute() {
        ArrayList<Route> listRoute = new ArrayList<Route>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Route route = new Route();
                route.setId(cursor.getInt(0));
                Log.d("abcde","cursor " +cursor.getInt(0)+"  "+cursor.getString(1) +" " +cursor.getLong(4) );
                route.setName(cursor.getString(1));
                byte[] blob = cursor.getBlob(2);
                String json = new String(blob);
                Gson gson = new Gson();
                ArrayList<Child> children = gson.fromJson(json, new TypeToken<ArrayList<Child>>()
                {}.getType());
                route.setListChildren(children);
                route.setIcon(cursor.getInt(3));
                route.setTimeCurrent(cursor.getLong(4));
                route.setTime(cursor.getLong(5));
                listRoute.add(route);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listRoute;
    }
    /*
    Delete a Child by ID
     */
    public void deleteRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[] { String.valueOf(route.getId()) });
        db.close();
    }
    /*
    Get Count Child in Table Child
     */
    public int getRouteCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }
}
