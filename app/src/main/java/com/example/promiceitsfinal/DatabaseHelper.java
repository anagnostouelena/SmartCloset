package com.example.promiceitsfinal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.List;


class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "closettttttttttttttt";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE clothes ("
                + "clothes_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "season varchar(45),"
                + "occasion varchar(45),"
                + "category varchar(45),"
                + "image_id integer);"
        );

        db.execSQL("CREATE TABLE images ("
                + "image_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "image BLOB);"
        );

        db.execSQL("CREATE TABLE sets ("
                + "set_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ref_id int,"
                + "date Date);"
        );

        db.execSQL("CREATE TABLE refs ("
                + "ref_id INTEGER , "
                + "clothes_id int);"

        );


    }

    public void insertClothes(SQLiteDatabase db,
                              String season,
                              String occasion,
                              String category) {
        ContentValues values = new ContentValues();
        values.put("season", season);
        values.put("occasion", occasion);
        values.put("category", category);


        String query = "SELECT last_insert_rowid() FROM images";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            long lastRowId = cursor.getLong(0);
            values.put("image_id", lastRowId);}
        cursor.close();


        db.insert("clothes", null, values);

    }

    public void insertImages(SQLiteDatabase db, byte[] image) {
        ContentValues values = new ContentValues();
        values.put("image", image);
        db.insert("images", null, values);
    }

    public void insertSets(SQLiteDatabase db, String date, int ref_id) {
        ContentValues values = new ContentValues();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
//        String formattedDate = dateFormat.format(date);

        values.put("date", date);
        values.put("ref_id", ref_id);
//        db.execSQL("ALTER TABLE  refs" +
//                "        ADD date DATE;"
//        );

        db.insert("sets", null, values);
    }

    public void insertRef(SQLiteDatabase db,int ref_id,int clothes_id ) {
        ContentValues values = new ContentValues();


//        db.execSQL("ALTER TABLE  refs" +
//                "        ADD date DATE;"
//        );
//        values.put("randomId", randomId);
        values.put("clothes_id", clothes_id);
        values.put("ref_id", ref_id);
//        values.put("date", date);

        db.insert("refs", null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }



    // below is the method for updating our courses
    public boolean updateRecord(String imageId,String season, String occasion, String category){

        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Insert the corrected values in the corresponding field
        values.put("season", season);
        values.put("occasion",occasion);
        values.put("category", category);

        //update the values of the specific record with the specific image_id
        db.update("clothes", values, "image_id = ?", new String[] { imageId });

        //close the database
        db.close();
        return true;
    }

    public boolean deleteData(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.remove("season");
        values.remove("occasion");
        values.remove("category");

        Log.d("value", String.valueOf(values));
        db.delete("clothes", "image_id="+imageId, null);
        db.delete("images", "image_id="+imageId, null);

        db.close();
        return true;
    }
    public List<Integer> findSetByDate(Date date){
        return null;
    }


}
