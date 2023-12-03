package com.example.dwelventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PhotoCustomList extends ArrayAdapter<Bitmap> {
    private ArrayList<Bitmap> photos;
    private Context context;


    public PhotoCustomList(Context context, ArrayList<Bitmap> photos){
        super(context,0,photos);
        this.photos = photos;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_photo_item,parent,false);
        }

        Bitmap photo = photos.get(position);
        ImageView photoView = view.findViewById(R.id.indiv_image);
        photoView.setImageBitmap(photo);
        return view;
    }
}
