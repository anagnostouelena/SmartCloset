package com.example.myapplication;

import static com.example.myapplication.Calendar.gridViewCalendar;
import static com.example.myapplication.MainActivity.gridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {

    CalendarView calendarview;
    TextView date_view;
    String dateCalendar;
    public static GridView gridViewCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

         calendarview = findViewById(R.id.calendarView);
        date_view =findViewById(R.id.date_view);
        gridViewCalendar =findViewById(R.id.grid_view_calendar);

         calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
             @Override
             public void onSelectedDayChange(@NonNull CalendarView calendarView, int day, int month , int year) {
                 // Add +1 in month because month index is start from 0
                  dateCalendar  = day + "-" + (month + 1) + "-" + year;
                 date_view.setText(dateCalendar);

                 MyTaskCalendar myTaskCalendar = new MyTaskCalendar(Calendar.this,dateCalendar);
                 myTaskCalendar.execute();
             }
         });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.calledar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottomHome:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;

                case R.id.randomOutfit:
                    startActivity(new Intent(getApplicationContext(), RandomOutfits.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;

                case R.id.calledar:

                    return true;
            }
            return false;
        });
    }
}
class MyTaskCalendar extends AsyncTask<Void,Void,Void>{
    ImageAdapter myAdapterCaledar;
    Context ctx;
    List<Bitmap> listOfBitmapClothesCaledar;
    SQLiteDatabase dbReadCaledar;
    DatabaseHelper databaseHelperCaledar;
    List<String> image_ids_Calendar;
    Bitmap imagesCalendar;
    String dateCalendar;



    public MyTaskCalendar(Context ctx,String dateCalendar ){
        this.ctx=ctx;
        this.dateCalendar=dateCalendar;
        listOfBitmapClothesCaledar =new ArrayList<>();
    }

    @SuppressLint("Range")
    @Override
    protected Void doInBackground(Void... params) {

        databaseHelperCaledar = new DatabaseHelper(ctx);
        dbReadCaledar = databaseHelperCaledar.getReadableDatabase();

        String[] columsFromRefTable = {"ref_id","clothes_id"};
        String[] columsFromSetTable = {"set_id","ref_id","date"};
        String[] columnsFromImagesTable = {"image"};

        Cursor cursorFromRefTable = dbReadCaledar.query("refs", columsFromRefTable, null, null, null, null, null);
        Cursor cursorFromSetTable = dbReadCaledar.query("sets", columsFromSetTable, null, null, null, null, null);
        Cursor cursorFromImagesTable = dbReadCaledar.query("images", columnsFromImagesTable, null, null, null, null, null);

        String date;
        String ref_idSet = "";
        while (cursorFromSetTable.moveToNext()) {
            date = cursorFromSetTable.getString(cursorFromSetTable.getColumnIndex("date"));

            if(date.equals(dateCalendar)){
                ref_idSet = cursorFromSetTable.getString(cursorFromSetTable.getColumnIndex("ref_id"));
                Log.d("ref_idSet1",ref_idSet);
            }
        }
        String clothes_id="";
        String ref_id ="";
        while (cursorFromRefTable.moveToNext()) {
            ref_id = cursorFromRefTable.getString(cursorFromRefTable.getColumnIndex("ref_id"));


            if (ref_id.equals(ref_idSet)) {
                clothes_id = cursorFromRefTable.getString(cursorFromRefTable.getColumnIndex("clothes_id"));
                cursorFromImagesTable.moveToPosition(Integer.parseInt(clothes_id) - 1);


                @SuppressLint("Range") byte[] imageBytess = cursorFromImagesTable.getBlob(cursorFromImagesTable.getColumnIndex("image"));
                imagesCalendar = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
                listOfBitmapClothesCaledar.add(imagesCalendar);

            }
        }
        
        cursorFromSetTable.close();
        cursorFromRefTable.close();
        cursorFromImagesTable.close();
        myAdapterCaledar = new ImageAdapter(ctx, listOfBitmapClothesCaledar);

        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        gridViewCalendar.setAdapter(myAdapterCaledar);
    }
}