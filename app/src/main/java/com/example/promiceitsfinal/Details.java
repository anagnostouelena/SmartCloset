package com.example.promiceitsfinal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Details extends AppCompatActivity {
    ImageView imageViewCameraGallery;
    public  static Spinner spinnerSeason;
    public  static Spinner occationspinner;
    public  static Spinner spinnerCategories;
    Button buttonOK;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    public static boolean saved;
    Button deleteButtonDetails,updateTheRecord;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        bundle = getIntent().getExtras();
        saved=false;

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        imageViewCameraGallery = findViewById(R.id.ImageViewCameraGallery);
        spinnerSeason = findViewById(R.id.spinnerSeason);
        occationspinner =findViewById(R.id.occationspinner);
        buttonOK = findViewById(R.id.buttonOK);
        spinnerCategories =findViewById(R.id.spinnerCategories);
        deleteButtonDetails = findViewById(R.id.deleteButtonDetails);
        updateTheRecord = findViewById(R.id.updateTheRecord);

        String action = getIntent().getAction();
        if(action!=null) {
            if (action.equals("com.example.myapplication.ACTION_MAIN_DATA_UPLOAD")) {
                updateTheRecord.setVisibility(View.GONE);
                deleteButtonDetails.setVisibility(View.GONE);
            }
            if(action.equals("com.example.myapplication.ACTION_UPDATE_DELETE")){
                buttonOK.setVisibility(View.GONE);
            }
        }

        if((bundle.getByteArray("image") != null)){
            byte[]  bytesImage = bundle.getByteArray("image");
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytesImage,0,bytesImage.length);
            imageViewCameraGallery.setImageBitmap(bitmapImage);
        }

        // permission for camera
        if(ContextCompat.checkSelfPermission(Details.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Details.this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }
        // permission for gallery
//        if(ContextCompat.checkSelfPermission(Details.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(Details.this,new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//            },1000);
//        }


        //save
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        if (MainActivity.imageBytes == null){
                        System.out.println("null");
                    }
                    databaseHelper.insertImages(db, MainActivity.imageBytes);
                    databaseHelper.insertClothes(db, spinnerSeason.getSelectedItem().toString() ,
                            occationspinner.getSelectedItem().toString(),spinnerCategories.getSelectedItem().toString());
                    Log.d("spinnerCategories",spinnerCategories.getSelectedItem().toString());
                    databaseHelper.close();
                    Toast.makeText(Details.this, "save in database", Toast.LENGTH_SHORT).show();
                    saved=true;
                    MainActivity.category = spinnerCategories.getSelectedItem().toString();
                    MainActivity.season = spinnerSeason.getSelectedItem().toString();
                    MainActivity.occasion = occationspinner.getSelectedItem().toString();

                }catch (Exception e){
                    e.printStackTrace();
                }
                finish();
            }
        });






        //setOnClickListener the button update
        updateTheRecord.setOnClickListener(view -> {

            //take the specific image id  the the user choose to change
            String id = bundle.getString("imageid");

            //update the record with  new fields
            boolean isUpdate = databaseHelper.updateRecord(id,spinnerSeason.getSelectedItem().toString() ,
                    occationspinner.getSelectedItem().toString(),spinnerCategories.getSelectedItem().toString());

            //and check if Data Updated or not
            if(isUpdate)
                Toast.makeText(Details.this,"Data Updated",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(Details.this,"Data Not Updated",Toast.LENGTH_LONG).show();
            finish();

        });



        deleteButtonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = bundle.getString("imageid");
               boolean isDelete = databaseHelper.deleteData(id);
//                imageViewCameraGallery.setImageBitmap(null);
//                MainActivity.gridView.removeView(imageViewCameraGallery);
                imageViewCameraGallery=null;
                if(isDelete)
                    Toast.makeText(Details.this,"Data Deleted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(Details.this,"Data Not Deleted",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
