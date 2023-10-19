package com.example.promiceitsfinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton generalFab;
    FloatingActionButton cameraFab;
    FloatingActionButton galleryFab;
    Boolean isAllFabsVisible;
    public static Uri uri;
    public static Bitmap bitmapCamera;
    Bitmap bitmapGallery;
    public static byte[] imageBytes;
    public static Bitmap compressedImgCamera,compressedImgGallery;
    List<Bitmap> ListOfBitmapClothes;
    public  static String category;
    public  static String season;
    public  static String occasion;
    public  static  GridView gridView;

    List<String> selectedCategories;




    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListOfBitmapClothes = new ArrayList<>();
        selectedCategories = new ArrayList<>();

        generalFab = findViewById(R.id.generalFab);
        cameraFab = findViewById(R.id.camera);
        galleryFab = findViewById(R.id.gallery);
       gridView = findViewById(R.id.grid_view);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomHomee);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomHomee:
                    return true;

                case R.id.randomOutfitt:
                    startActivity(new Intent(getApplicationContext(), RandomOutfits.class));
                    //anim that i made
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;

                case R.id.calendar:
                    startActivity(new Intent(getApplicationContext(), Calendar.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });

        MyTask myTask = new MyTask(MainActivity.this);
        myTask.execute();

        cameraFab.setVisibility(View.GONE);
        galleryFab.setVisibility(View.GONE);

        isAllFabsVisible = false;

        generalFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                cameraFab.show();
                galleryFab.show();
                isAllFabsVisible = true;
            } else {
                cameraFab.hide();
                galleryFab.hide();
                isAllFabsVisible = false;
            }
        });

        //fab cameras
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
            }

        });

        //fabe gallery
        galleryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageGalery = new Intent(Intent.ACTION_PICK);
                imageGalery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageGalery, 1000);
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Bitmap>  bitmapclothes = myTask.getListOfBitmapClothes();
                Bitmap bitmapImage = bitmapclothes.get(i);
                OpenDetailsFromImageViewToUpdateAndDelete odfivtuad = new OpenDetailsFromImageViewToUpdateAndDelete(bitmapImage,ImageAdapter.imageIds.get(i));
                view.setOnClickListener(odfivtuad);

            }
        });


    }

    //tool bar

//tool bar
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.toolbarfilter:
//                startActivityForResult(new Intent(getApplicationContext(),FilterChoises.class),123);
//
//                break;
////            case R.id.toolDelete:
////
////                break;
////            case R.id.toolMultiplesPick:
////
////                break;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent(getApplicationContext(),Details.class);
        intent.setAction("com.example.myapplication.ACTION_MAIN_DATA_UPLOAD");

        //camera
        if(requestCode==100){
            if (data != null) {
                bitmapCamera = (Bitmap) data.getExtras().get("data");

                int desiredWidth = 600;
                int desiredHeight = 800;

                float scaleWidth = ((float) desiredWidth) / bitmapCamera.getWidth();
                float scaleHeight = ((float) desiredHeight) / bitmapCamera.getHeight();

                // Create a matrix for the scaling and apply the scale factors
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmapCamera, 0, 0, bitmapCamera.getWidth(), bitmapCamera.getHeight(), matrix, false);
                ByteArrayOutputStream outputStreamCamera = compress(resizedBitmap);
                imageBytes = outputStreamCamera.toByteArray();
                intent.putExtra("image",imageBytes);
                compressedImgCamera = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            }
        }
        //gallery
        if(resultCode==RESULT_OK) {
            if (requestCode == 1000) {
                uri = data.getData();

                try {
                    bitmapGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int desiredWidth = 600;
                int desiredHeight = 800;

                float scaleWidth = ((float) desiredWidth) / bitmapGallery.getWidth();
                float scaleHeight = ((float) desiredHeight) / bitmapGallery.getHeight();

                // Create a matrix for the scaling and apply the scale factors
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmapGallery = Bitmap.createBitmap(bitmapGallery, 0, 0, bitmapGallery.getWidth(), bitmapGallery.getHeight(), matrix, false);
                ByteArrayOutputStream outputStreamGallery = compress(resizedBitmapGallery);
                imageBytes = outputStreamGallery.toByteArray();
                intent.putExtra("image",imageBytes);
                compressedImgGallery = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            }
        }
        startActivity(intent);
//        if(requestCode==123 && data!=null) {
//
//             dressIsChecked = data.getBooleanExtra("dress", true);
//             JeansIsChecked = data.getBooleanExtra("Jeans", true);
//             CoatIsChecked = data.getBooleanExtra("Coat", true);
//             shortsIsChecked = data.getBooleanExtra("shorts", true);
//             SkirtsIsChecked = data.getBooleanExtra("Skirts", true);
//             TopsIsChecked = data.getBooleanExtra("Tops", true);
//             ShoesIsChecked = data.getBooleanExtra("Shoes", true);
//             BagsIsChecked = data.getBooleanExtra("Bags", true);
//             AccessoriesIsChecked = data.getBooleanExtra("Accessories", true);
//             BoufanIsChecked = data.getBooleanExtra("Boufan", true);
//             JacketsIsChecked = data.getBooleanExtra("Jackets", true);
//             WinterIsChecked = data.getBooleanExtra("Winter", true);
//             AutumnIsChecked = data.getBooleanExtra("Autumn", true);
//             SummerIsChecked = data.getBooleanExtra("Summer", true);
//             SpringIsChecked = data.getBooleanExtra("Spring", true);
//             WorkIsChecked = data.getBooleanExtra("Work", true);
//             Formal_eventsIsChecked = data.getBooleanExtra("Formal_events", true);
//             CasualIsChecked = data.getBooleanExtra("Casual", true);
//             SportsIsChecked = data.getBooleanExtra("Sports", true);
//             Special_OccasionIsChecked = data.getBooleanExtra("Special_Occasion", true);
//             Cultural_EventIsChecked = data.getBooleanExtra("Cultural_Event", true);
//            selectedCategories.add(String.valueOf(data));
//            Log.d("selectedcategories", selectedCategories.toString());
//
//        }

    }
//package:com.example.myapplication
    private ByteArrayOutputStream compress(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        MyTask myTask = new MyTask(MainActivity.this);
        myTask.execute();

    }
}

class MyTask extends AsyncTask<Void, Void, Void> {
    ImageAdapter myAdapter;
    Context ctx;
    List<Bitmap> listOfBitmapClothes;
    SQLiteDatabase dbRead;
    DatabaseHelper databaseHelper;
    List<String> image_ids ;


    public MyTask(Context ctx ){
        this.ctx=ctx;
        listOfBitmapClothes =new ArrayList<>();
    }

    public List<Bitmap> getListOfBitmapClothes() {
        return listOfBitmapClothes;
    }

    @Override
    protected Void doInBackground(Void... params) {

        databaseHelper = new DatabaseHelper(ctx);
        dbRead = databaseHelper.getReadableDatabase();
        image_ids=new ArrayList<>();


        String[] columnsFromClothesTable = {"clothes_id","category", "image_id", "season", "occasion"};
        String[] columnsFromImagesTable = {"image"};

        Cursor cursorForClothesTable = dbRead.query("clothes", columnsFromClothesTable, null, null, null, null, null);
        Cursor cursorForImagesTable = dbRead.query("images", columnsFromImagesTable, null, null, null, null, null);

        Log.d("count", String.valueOf(cursorForImagesTable.getCount()));

        while (cursorForClothesTable.moveToNext()) {

            int position = cursorForClothesTable.getPosition();
            cursorForImagesTable.moveToPosition(position);
            @SuppressLint("Range") byte[] imageBytess = cursorForImagesTable.getBlob(cursorForImagesTable.getColumnIndex("image"));
            @SuppressLint("Range") String image_id = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("image_id"));
            @SuppressLint("Range") String clothes_id = cursorForClothesTable.getString(cursorForClothesTable.getColumnIndex("clothes_id"));

            Bitmap image = BitmapFactory.decodeByteArray(imageBytess, 0, imageBytess.length);
            listOfBitmapClothes.add(image);
            image_ids.add(image_id);
            Log.d("clothes_id_from_Main",clothes_id);
            Log.d("cursorForClothesTable", String.valueOf(cursorForClothesTable.getPosition()));
        }

        cursorForClothesTable.close();
        cursorForImagesTable.close();
        myAdapter = new ImageAdapter(ctx, listOfBitmapClothes);
        myAdapter.setImageIds(image_ids);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        MainActivity.gridView.setAdapter(myAdapter);
    }
}





