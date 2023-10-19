package com.example.promiceitsfinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

class ImageAdapter extends BaseAdapter{
    public static Context mContext;
    private final List<Bitmap> bitmaps;
    boolean[] selectedItems;
    boolean showCheckboxes;
    public  static List<String> imageIds;


    public ImageAdapter(Context context, List<Bitmap> bitmaps) {
        mContext = context;
        this.bitmaps = bitmaps;
        this.selectedItems = new boolean[bitmaps.size()];
        this.showCheckboxes = false;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }

    @SuppressLint("ViewHolder")
    public View getView(int index, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder =new ViewHolder();
        view = inflater.inflate(R.layout.images, viewGroup, false);
        viewHolder.image = view.findViewById(R.id.imagetest);
        viewHolder.image.setImageBitmap(bitmaps.get(index));

//        Bitmap bitmap = bitmaps.get(index);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        byte[] byteArray = outputStream.toByteArray();


//        OpenDetailsFromImageViewToUpdateAndDelete odfivtuad = new OpenDetailsFromImageViewToUpdateAndDelete(byteArray,imageIds.get(index));
//        viewHolder.image.setOnClickListener(odfivtuad);

        return view;
    }
    static class ViewHolder {
        ImageView image;

    }




}





