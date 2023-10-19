package com.example.promiceitsfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class OpenDetailsFromImageViewToUpdateAndDelete implements View.OnClickListener {

    Bitmap image;
    String image_id;
    public OpenDetailsFromImageViewToUpdateAndDelete(Bitmap image, String image_id){
        this.image = image;
        this.image_id = image_id;
    }

    @Override
    public void onClick(View view) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        byte[] byteImage = byteArrayOutputStream.toByteArray();


        Context context = view.getContext();
        Intent intent = new Intent(context, Details.class);
        intent.setAction("com.example.myapplication.ACTION_UPDATE_DELETE");
        intent.putExtra("image",byteImage);
        intent.putExtra("imageid",image_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
