package com.example.promiceitsfinal;


import static com.example.myapplication.RandomOutfits.occationspinnerAPi;
import static com.example.myapplication.RandomOutfits.spinnerCategoriesAPI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RandomOutfits extends AppCompatActivity {

    public  static Spinner occationspinnerAPi,spinnerCategoriesAPI,seasonspinnerAPi;
    public  static Button buttonOKAPi,favourite;
    public static GridView gridViewApi;
    List<Bitmap> listOfBitmapClothesApi;
    MyTaskAPI myTaskAPI;
    public static List<Integer> allrandom;


    SQLiteDatabase db;

    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_outfits);

        listOfBitmapClothesApi = new ArrayList<>();
        allrandom = new ArrayList();


        occationspinnerAPi = findViewById(R.id.occationspinnerAPi);
        spinnerCategoriesAPI = findViewById(R.id.spinnerCategoriesAPI);
        buttonOKAPi = findViewById(R.id.buttonOKAPi);
        gridViewApi = findViewById(R.id.grid_view_api);
        favourite = findViewById(R.id.favourite);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.randomOutfit);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottomHome:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;

                case R.id.randomOutfit:
                    return true;

                case R.id.calledar:
                    startActivity(new Intent(getApplicationContext(), Calendar.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });



        listOfBitmapClothesApi =new ArrayList<>();
        buttonOKAPi.setOnClickListener((view)->{
            myTaskAPI = new MyTaskAPI(RandomOutfits.this,listOfBitmapClothesApi);
            myTaskAPI.execute();

        });
        favourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Date currentDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);
                Log.d("formattedDate", formattedDate);

                databaseHelper =new DatabaseHelper(RandomOutfits.this);
                db = databaseHelper.getWritableDatabase();


                String[]  columnsFromRefTable = {"ref_id", "clothes_id"};
                Cursor cursorForRefTable = db.query("refs", columnsFromRefTable, null, null, null, null, null);
                int lastId = 0;

                if (cursorForRefTable.moveToLast()) {
                     lastId =  cursorForRefTable.getInt(0);
                    Log.d("lastId", String.valueOf(lastId));
                }
                lastId++;

                for( int item : allrandom){
                    databaseHelper.insertRef(db,lastId,item);
                    Log.d("lastIdfor", String.valueOf(lastId));
                    Log.d("iteminsertRef", String.valueOf(item));

                }
                databaseHelper.insertSets(db,formattedDate,lastId);



                Toast.makeText(RandomOutfits.this, "Saved to favourite", Toast.LENGTH_SHORT).show();

              databaseHelper.close();


            }
        });
    }

}
class MyTaskAPI extends AsyncTask<Void, Void, String> {
    Context ctxapi;
    ProgressDialog progressDialog;
    List<String> summerListIDs;
    List<String> springListIDs;
    List<String> autowmListIDs;
    List<String> winterListIDs;
    SQLiteDatabase dbApi;
    DatabaseHelper databaseHelperApi;
    List<Bitmap> listOfBitmapClothesApi;
    ImageAdapter myAdapterApi;
    Bitmap imageApi;
    Boolean itemFound = false;
    float tempC;
    public static int curentId;
    public static int randomPickImageSpring, randomPickImageSummer, randomPickImageWinter, randomPickImageAutom;

    public MyTaskAPI(Context ctxapi, List<Bitmap> listOfBitmapClothesApi) {
        this.ctxapi = ctxapi;
        this.listOfBitmapClothesApi = listOfBitmapClothesApi;
        randomPickImageSpring = -1;
        randomPickImageSummer = -1;
        randomPickImageWinter = -1;
        randomPickImageAutom = -1;
    }


    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(ctxapi);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        super.onPreExecute();
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected String doInBackground(Void... params) {


        databaseHelperApi = new DatabaseHelper(ctxapi);
        dbApi = databaseHelperApi.getReadableDatabase();


        Random random = new Random();
//http://api.weatherapi.com/v1/current.json?key=54331e32200048df87d91626230505&q&q&q&q=Larisa&aqi=yes
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("http://api.weatherapi.com/v1/current.json?key=54331e32200048df87d91626230505&q&q&q=Kalampaka&aqi=yes")
                .method("GET", null).build();
        Log.d("request", String.valueOf(request));
        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONObject current = new JSONObject(json.getString("current"));
            tempC = Float.parseFloat(current.getString("temp_c"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        String[] columnsFromClothesTable = {"category", "image_id", "season", "occasion"};
        String[] columnsFromImagesTable = {"image_id", "image"};

        String selectedCategory = (String) RandomOutfits.spinnerCategoriesAPI.getSelectedItem();
        String selectedOccasion = (String) RandomOutfits.occationspinnerAPi.getSelectedItem();


        Cursor cursorForClothesTable = dbApi.query("clothes", columnsFromClothesTable, null, null, null, null, null);
        Cursor cursorForImagesTable = dbApi.query("images", columnsFromImagesTable, null, null, null, null, null);

//        itemListStringImageId = new ArrayList<>();
        winterListIDs = new ArrayList<>();
        springListIDs = new ArrayList<>();
        autowmListIDs = new ArrayList<>();
        summerListIDs = new ArrayList<>();


        while (cursorForClothesTable.moveToNext()) {
            @SuppressLint("Range") String category = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("category"));
            @SuppressLint("Range") String occasion = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("occasion"));
            @SuppressLint("Range") String image_id = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("image_id"));
            @SuppressLint("Range") String season = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("season"));

            if (category.equals(selectedCategory)) {
                if (occasion.equals(selectedOccasion)) {
                    if (season.equals("Summer")) {
                        summerListIDs.add(image_id);
                    }
                    if (season.equals("Spring")) {
                        springListIDs.add(image_id);
//                        Log.d("springlist", springListIDs.toString());

                    }
                    if (season.equals("autumn")) {
                        autowmListIDs.add(image_id);

                    }
                    if (season.equals("Winter")) {
                        winterListIDs.add(image_id);

                    }

                }
            }
        }
        while (cursorForImagesTable.moveToNext()) {

            curentId = cursorForImagesTable.getInt(0);

            //Summer
            if (tempC >= 30.0) {
                if (summerListIDs.size() > 0) {
                    itemFound = true;

                    int randomIndexSummer = random.nextInt(summerListIDs.size());
                    randomPickImageSummer = Integer.parseInt(summerListIDs.get(randomIndexSummer));
                    RandomOutfits.allrandom.add(randomPickImageSummer);

                    if (curentId == randomPickImageSummer) {
//                cursorForImagesTable.moveToPosition(randomPickImageSummer - 1);
                        @SuppressLint("Range") byte[] imageBytess = cursorForImagesTable.getBlob(cursorForImagesTable.getColumnIndex("image"));
                        imageApi = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
                        listOfBitmapClothesApi.add(imageApi);
                    }
                } else {
                    itemFound = false;

                }
            }
            //Spring
            if (tempC >= 21.0 && tempC <= 29.9) {
                if (springListIDs.size() > 0) {
                    itemFound = true;
                    int randomIndexSpring = random.nextInt(springListIDs.size());
                    randomPickImageSpring = Integer.parseInt(springListIDs.get(randomIndexSpring));
                    Log.d("randomPickImagefromSpring", String.valueOf(randomPickImageSpring));
                    RandomOutfits.allrandom.add(randomPickImageSpring);

//                cursorForImagesTable.moveToPosition(randomPickImageSpring - 1);
                    if (curentId == randomPickImageSpring) {

                        @SuppressLint("Range") byte[] imageBytess = cursorForImagesTable.getBlob(cursorForImagesTable.getColumnIndex("image"));
                        imageApi = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
                        listOfBitmapClothesApi.add(imageApi);

                    }
                } else {
                    itemFound = false;
                }
            }


            //Autom
            if (tempC >= 11.0 && tempC <= 20.9) {
                if (autowmListIDs.size() > 0) {
                    itemFound = true;

                    int randomIndexAutom = random.nextInt(autowmListIDs.size());
                    randomPickImageAutom = Integer.parseInt(autowmListIDs.get(randomIndexAutom));
                    Log.d("randomPickImageAutom", String.valueOf(randomPickImageAutom));
                    RandomOutfits.allrandom.add(randomPickImageAutom);

//                cursorForImagesTable.moveToPosition(randomPickImageAutom-1);
                    if (curentId == randomPickImageAutom) {
                        @SuppressLint("Range") byte[] imageBytess = cursorForImagesTable.getBlob(cursorForImagesTable.getColumnIndex("image"));
                        imageApi = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
                        listOfBitmapClothesApi.add(imageApi);
                    }

                } else {
                    itemFound = false;
                }
            }
            //winter
            if (tempC >= 0.0 && tempC <= 10.0) {
                if (winterListIDs.size() > 0) {
                    itemFound = true;
                    int randomIndexWinter = random.nextInt(winterListIDs.size());
                    randomPickImageWinter = Integer.parseInt(winterListIDs.get(randomIndexWinter));
                    RandomOutfits.allrandom.add(randomPickImageWinter);

//                    cursorForImagesTable.moveToPosition(randomPickImageWinter - 1);
                    if (curentId == randomPickImageWinter) {
                        @SuppressLint("Range") byte[] imageBytess = cursorForImagesTable.getBlob(cursorForImagesTable.getColumnIndex("image"));
                        imageApi = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
                        listOfBitmapClothesApi.add(imageApi);
                    }

                } else {
                    itemFound = false;
                }
            }
        }


            cursorForClothesTable.close();
            cursorForImagesTable.close();
            myAdapterApi = new ImageAdapter(ctxapi, listOfBitmapClothesApi);


            return null;

    }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);

            progressDialog.dismiss();
            if (!itemFound) {
                Toast.makeText(ctxapi, "Item not found", Toast.LENGTH_LONG).show();
            }
            RandomOutfits.gridViewApi.setAdapter(myAdapterApi);
        }
    }


