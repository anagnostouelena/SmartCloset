package com.example.promiceitsfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class ClickListenerImageView implements View.OnClickListener{
    Bitmap bitmap;

   public ClickListenerImageView(Bitmap bitmap){
       this.bitmap=bitmap;
   }
    @Override
    public void onClick(View view) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();
        Context context = view.getContext();
        Intent intent = new Intent(context, Details.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("image",bytes);
        context.startActivity(intent);
    }
}
